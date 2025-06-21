package by.test.sample.exception;

import static by.test.sample.utils.ApplicationConstants.ACCOUNT_NOT_FOUND_MESSAGE;

public class AccountNotFoundException extends LocalException {

    public AccountNotFoundException(Long userId) {
        super(ACCOUNT_NOT_FOUND_MESSAGE);
    }
}
