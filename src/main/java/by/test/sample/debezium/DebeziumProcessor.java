package by.test.sample.debezium;

import by.test.sample.enums.DebeziumOperationType;
import by.test.sample.enums.DebeziumTable;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class DebeziumProcessor {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    public abstract void process(String table, JsonNode node);

    protected abstract DebeziumOperationType getOperationType();

    protected Long getUserId(String source, JsonNode node) {
        if (node == null || node.isNull()) {
            return null;
        }
        return DebeziumTable.getByTableName(source)
                .map(table -> switch (table) {
                    case USERS -> node.has("id") ? node.get("id").asLong() : null;
                    case EMAIL, PHONE -> node.has("user_id") ? node.get("user_id").asLong() : null;
                })
                .orElse(null);
    }
}
