package by.test.sample.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public enum SearchEngineType {

    JPA("jpa"),

    ELASTIC("elastic");

    @Getter
    private final String title;

    private static final Map<String, SearchEngineType> SEARCH_ENGINE_MAP = Arrays.stream(SearchEngineType.values())
            .collect(Collectors.toMap(SearchEngineType::getTitle, Function.identity()));

    public static SearchEngineType getEngineByType(String engineType) {
        return SEARCH_ENGINE_MAP.getOrDefault(engineType, SearchEngineType.JPA);
    }
}
