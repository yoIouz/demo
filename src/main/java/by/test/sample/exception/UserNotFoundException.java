package by.test.sample.exception;

import static by.test.sample.utils.ApplicationConstants.USER_NOT_FOUND_MESSAGE;

public class UserNotFoundException extends LocalException {

    public UserNotFoundException() {
        super(USER_NOT_FOUND_MESSAGE);
    }
}
