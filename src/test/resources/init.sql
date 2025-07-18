DROP TABLE IF EXISTS ACCOUNT;
DROP TABLE IF EXISTS EMAIL_DATA;
DROP TABLE IF EXISTS PHONE_DATA;
DROP TABLE IF EXISTS USERS;

CREATE TABLE USERS
(
    ID            BIGSERIAL PRIMARY KEY,
    NAME          VARCHAR(500),
    DATE_OF_BIRTH DATE,
    PASSWORD      VARCHAR(500) CHECK (LENGTH(PASSWORD) >= 8)
);

CREATE TABLE ACCOUNT
(
    ID      BIGSERIAL PRIMARY KEY,
    USER_ID BIGINT,
    BALANCE DECIMAL,
    FOREIGN KEY (USER_ID) REFERENCES USERS (ID)
);

CREATE TABLE EMAIL_DATA
(
    ID      BIGSERIAL PRIMARY KEY,
    USER_ID BIGINT,
    EMAIL   VARCHAR(200) UNIQUE,
    FOREIGN KEY (USER_ID) REFERENCES USERS (ID)
);

CREATE TABLE PHONE_DATA
(
    ID      BIGSERIAL PRIMARY KEY,
    USER_ID BIGINT,
    PHONE   VARCHAR(13) UNIQUE,
    FOREIGN KEY (USER_ID) REFERENCES USERS (ID)
);