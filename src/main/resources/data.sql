DELETE FROM account;
DELETE FROM email_data;
DELETE FROM phone_data;
DELETE FROM users;

ALTER SEQUENCE users_id_seq RESTART WITH 1;
ALTER SEQUENCE phone_data_id_seq RESTART WITH 1;
ALTER SEQUENCE email_data_id_seq RESTART WITH 1;
ALTER SEQUENCE account_id_seq RESTART WITH 1;

INSERT INTO users(name, date_of_birth, password)
VALUES ('Иван Иванов', '1993-05-01', 'password123'),
       ('Сергей Сергеев', '1990-01-25', 'password456');

INSERT INTO phone_data (phone, user_id)
VALUES ('79201234567', 1),
       ('79203334455', 1),
       ('79205556677', 2);

INSERT INTO email_data (email, user_id)
VALUES ('ivan@example.com', 1),
       ('anna@domain.com', 2),
       ('ivan.work@example.com', 1);

INSERT INTO account (user_id, balance)
VALUES (1, 100.00),
       (2, 110.00);
