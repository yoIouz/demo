package by.test.sample.exception;

import static by.test.sample.utils.ApplicationConstants.INVALID_PASSWORD_MESSAGE;

public class InvalidPasswordException extends LocalException {

    public InvalidPasswordException() {
        super(INVALID_PASSWORD_MESSAGE);
    }
}
