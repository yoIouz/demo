package by.test.sample.enums;

public enum DebeziumOperationType {

    CREATE_UPDATE,

    DELETE;

    public static DebeziumOperationType getType(String operation) {
        switch (operation) {
            case "c", "r", "u" -> {
                return CREATE_UPDATE;
            }
            case "d" -> {
                return DELETE;
            }
            default -> {
                return null;
            }
        }
    }
}
