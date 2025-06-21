package by.test.sample.enums;

public enum SearchEngineType {

    JPA,

    ELASTIC;

    public static SearchEngineType getEngine(boolean useElastic) {
        return useElastic ? ELASTIC : JPA;
    }
}
