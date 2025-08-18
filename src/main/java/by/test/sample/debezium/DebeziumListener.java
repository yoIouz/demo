package by.test.sample.debezium;

import by.test.sample.enums.DebeziumOperationType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.debezium.engine.ChangeEvent;
import io.debezium.engine.DebeziumEngine;
import io.debezium.engine.format.Json;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
@Profile("!test")
public class DebeziumListener {

    private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

    private final io.debezium.config.Configuration debeziumConfig;

    private final Map<DebeziumOperationType, DebeziumProcessor> debeziumProcessors;

    private final ObjectMapper objectMapper;

    public DebeziumListener(io.debezium.config.Configuration debeziumConfig,
                            List<DebeziumProcessor> processors,
                            ObjectMapper objectMapper) {
        this.debeziumConfig = debeziumConfig;
        this.debeziumProcessors = processors.stream()
                .collect(Collectors.toMap(DebeziumProcessor::getOperationType, Function.identity()));
        this.objectMapper = objectMapper;
    }

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
                this.process(operation, table, payload);
            }
        } catch (Exception e) {
            log.error("Failed to handle change event", e);
        }
    }

    private void process(String operation, String table, JsonNode payload) {
        DebeziumOperationType operationType = DebeziumOperationType.getType(operation);
        Optional.ofNullable(debeziumProcessors.get(operationType))
                .ifPresentOrElse(debeziumProcessor -> {
                            log.info("Processing operation {} for table {}", operationType, table);
                            debeziumProcessor.process(table, payload);
                        },
                        () -> {
                            throw new RuntimeException("No debezium processor for operation " + operationType);
                        });
    }
}