CREATE TABLE IF NOT EXISTS accounts (
    account_id BIGINT NOT NULL AUTO_INCREMENT,
    email VARCHAR(60) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    nickname VARCHAR(16) NOT NULL,
    bio VARCHAR(255),
    status VARCHAR(255) NOT NULL,
    PRIMARY KEY (account_id)
);