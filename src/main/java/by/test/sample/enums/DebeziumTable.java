package by.test.sample.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Optional;

@Getter
@RequiredArgsConstructor
public enum DebeziumTable {

    USERS("users"),

    EMAIL("email_data"),

    PHONE("phone_data");

    private final String tableName;

    public static Optional<DebeziumTable> getByTableName(String tableName) {
        return Arrays.stream(values())
                .filter(table -> table.getTableName().equalsIgnoreCase(tableName)).
                findFirst();
    }

}
