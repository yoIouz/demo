package by.test.sample.debezium.impl;

import by.test.sample.debezium.DebeziumProcessor;
import by.test.sample.document.UserElasticDocument;
import by.test.sample.entity.User;
import by.test.sample.enums.DebeziumOperationType;
import by.test.sample.mapper.UserMapper;
import by.test.sample.repository.UserRepository;
import by.test.sample.service.ElasticsearchService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CreateUpdateProcessor extends DebeziumProcessor {

    private final ElasticsearchService<UserElasticDocument, Long> elasticsearchService;

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    @Override
    public void process(String table, JsonNode after) {
        if (after == null || after.isNull()) {
            return;
        }
        Long userId = this.getUserId(table, after);
        if (userId == null) return;

        Optional<User> optionalUser = userRepository.findWithRelationsById(userId);
        if (optionalUser.isEmpty()) {
            logger.warn("User not found for id = {}", userId);
            return;
        }

        User user = optionalUser.get();
        UserElasticDocument dto = userMapper.toUserElasticDto(user);
        elasticsearchService.index(dto);
    }

    @Override
    protected DebeziumOperationType getOperationType() {
        return DebeziumOperationType.CREATE_UPDATE;
    }
}
