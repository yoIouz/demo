package by.test.sample.utils;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

public final class ApplicationConstants {

    /// EXCEPTIONS
    public static final String EMAIL_TAKEN_MESSAGE = "Email is already exists";

    public static final String PHONE_TAKEN_MESSAGE = "Phone is already exists";

    public static final String ACCOUNT_NOT_FOUND_MESSAGE = "Account not found";

    public static final String INVALID_PASSWORD_MESSAGE = "Invalid password";

    public static final String ENGINE_NOT_FOUND_MESSAGE = "Search engine not found for type: ";

    public static final String USER_NOT_FOUND_MESSAGE = "User not found";

    public static final String SERVER_ERROR_MESSAGE = "Internal server error";

    public static final String TOKEN_ERROR_MESSAGE = "Invalid or expired token";

    public static final String TRANSFER_NEGATIVE_MESSAGE = "Transfer amount must be positive";

    public static final String BALANCE_NEGATIVE_MESSAGE = "Balance cannot be negative";

    public static final String SELF_TRANSFER_MESSAGE = "Cannot transfer to the same account";

    public static final String TRANSFER_REQUEST_MISSING_MESSAGE = "Request cannot be null";

    /// DEBEZIUM
    public static final String USERS_TABLE = "users";

    public static final String EMAIL_TABLE = "email_data";

    public static final String PHONE_TABLE = "phone_data";

    public static final String USERS_ELASTIC_INDEX = "users";

    /// REDIS
    public static final int REDIS_TTL_MINUTES = 5;

    public static final String REDIS_INITIAL_BALANCES_KEY = "initial_balances";

    /// GENERAL
    public static final String AUTHORIZATION_HEADER = "Authorization";

    public static final String TOKEN_BEARER = "Bearer ";

    public static final BigDecimal MAX_INITIAL_BALANCE_MULTIPLIER = new BigDecimal("2.07");

    public static final BigDecimal INITIAL_BALANCE_MULTIPLIER = new BigDecimal("0.10");

    public static final String TRANSFER_SUCCESSFUL = "Transfer successful";

    public static final DateTimeFormatter CUSTOM_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

}
