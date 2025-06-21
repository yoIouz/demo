package by.test.sample.exception;

import by.test.sample.enums.SearchEngineType;

import static by.test.sample.utils.ApplicationConstants.ENGINE_NOT_FOUND_MESSAGE;

public class SearchEngineNotFoundException extends LocalException {

    public SearchEngineNotFoundException(SearchEngineType type) {
        super(ENGINE_NOT_FOUND_MESSAGE + type);
    }
}
