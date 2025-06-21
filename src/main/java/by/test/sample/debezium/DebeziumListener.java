package by.test.sample.debezium;

import by.test.sample.document.UserElasticDocument;
import by.test.sample.entity.User;
import by.test.sample.mapper.UserMapper;
import by.test.sample.repository.UserRepository;
import by.test.sample.service.ElasticsearchService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.debezium.engine.ChangeEvent;
import io.debezium.engine.DebeziumEngine;
import io.debezium.engine.format.Json;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static by.test.sample.utils.ApplicationConstants.EMAIL_TABLE;
import static by.test.sample.utils.ApplicationConstants.PHONE_TABLE;
import static by.test.sample.utils.ApplicationConstants.USERS_TABLE;

@Slf4j
@Component
@Profile("!test")
@RequiredArgsConstructor
public class DebeziumListener {

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final io.debezium.config.Configuration debeziumConfig;

    private final ElasticsearchService<UserElasticDocument, Long> elasticsearchService;

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    @PostConstruct
    public void start() {
        executor.submit(() -> {
            DebeziumEngine<ChangeEvent<String, String>> engine = DebeziumEngine.create(Json.class)
                    .using(debeziumConfig.asProperties())
                    .notifying((records, committer) -> {
                        for (ChangeEvent<String, String> record : records) {
                            this.handleChange(record);
                        }
                        log.info("Processed batch of {} records, indexed into Elasticsearch", records.size());
                        committer.markBatchFinished();
                    })
                    .build();
            try (engine) {
                engine.run();
            } catch (IOException e) {
                log.warn("Couldn't start debezium engine", e);
            }
        });
    }

    @PreDestroy
    public void stop() {
        executor.shutdownNow();
    }

    private void handleChange(ChangeEvent<String, String> changeEvent) {
        try {
            if (changeEvent != null && changeEvent.value() != null) {
                JsonNode payload = objectMapper.readTree(changeEvent.value()).get("payload");
                String operation = payload.get("op").asText();
                String table = payload.get("source").get("table").asText();

                switch (operation) {
                    case "c", "r", "u" -> this.handleCreateOrUpdate(table, payload.get("after"));
                    case "d" -> this.handleDelete(table, payload.get("before"));
                    default -> log.warn("Unknown operation '{}' in Debezium event, skipping", operation);
                }
            }
        } catch (Exception e) {
            log.error("Failed to handle change event", e);
        }
    }

    private void handleCreateOrUpdate(String table, JsonNode after) {
        if (after == null || after.isNull()) {
            return;
        }
        Long userId = this.getUserId(table, after);
        if (userId == null) return;

        Optional<User> optionalUser = userRepository.findWithRelationsById(userId);
        if (optionalUser.isEmpty()) {
            log.warn("User not found for id = {}", userId);
            return;
        }

        User user = optionalUser.get();
        UserElasticDocument dto = userMapper.toUserElasticDto(user);
        elasticsearchService.index(dto);
    }

    private void handleDelete(String table, JsonNode before) {
        if (before == null || before.isNull()) {
            return;
        }
        Long userId = this.getUserId(table, before);
        if (userId == null) return;

        if (USERS_TABLE.equals(table)) {
            elasticsearchService.delete(userId);
            log.info("Elasticsearch user document deleted: {}", userId);
        } else {
            Optional<User> optionalUser = userRepository.findWithRelationsById(userId);
            if (optionalUser.isEmpty()) {
                log.warn("User not found for id = {}", userId);
                return;
            }
            UserElasticDocument dtoUpdate = userMapper.toUserElasticDto(optionalUser.get());
            elasticsearchService.index(dtoUpdate);
            log.info("Elasticsearch user document updated after related deletion: {}", userId);
        }
    }

    private Long getUserId(String source, JsonNode node) {
        return switch (source) {
            case USERS_TABLE -> node.get("id").asLong();
            case EMAIL_TABLE, PHONE_TABLE -> node.get("user_id").asLong();
            default -> null;
        };
    }
}