package by.test.sample.debezium.impl;

import by.test.sample.debezium.DebeziumProcessor;
import by.test.sample.document.UserElasticDocument;
import by.test.sample.entity.User;
import by.test.sample.enums.DebeziumOperationType;
import by.test.sample.enums.DebeziumTable;
import by.test.sample.mapper.UserMapper;
import by.test.sample.repository.UserRepository;
import by.test.sample.service.ElasticsearchService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DeleteProcessor extends DebeziumProcessor {

    private final ElasticsearchService<UserElasticDocument, Long> elasticsearchService;

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    @Override
    protected void process(String table, JsonNode before) {
        if (before == null || before.isNull()) {
            return;
        }
        Long userId = this.getUserId(table, before);
        if (userId == null) return;

        if (DebeziumTable.USERS.getTableName().equals(table)) {
            elasticsearchService.delete(userId);
            logger.info("Elasticsearch user document deleted: {}", userId);
        } else {
            Optional<User> optionalUser = userRepository.findWithRelationsById(userId);
            if (optionalUser.isEmpty()) {
                logger.warn("User not found for id = {}", userId);
                return;
            }
            UserElasticDocument dtoUpdate = userMapper.toUserElasticDto(optionalUser.get());
            elasticsearchService.index(dtoUpdate);
            logger.info("Elasticsearch user document updated after related deletion: {}", userId);
        }
    }

    @Override
    protected DebeziumOperationType getOperationType() {
        return DebeziumOperationType.DELETE;
    }
}
