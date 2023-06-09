DROP TABLE IF EXISTS USER_ENTITY;
CREATE TABLE IF NOT EXISTS USER_ENTITY
(
    ID UUID DEFAULT gen_random_uuid() NOT NULL PRIMARY KEY,
    FIRST_NAME VARCHAR(50) NOT NULL,
    LAST_NAME VARCHAR(50) NOT NULL,
    PASSWORD VARCHAR(500) NOT NULL,
    ADDRESS VARCHAR(100) NOT NULL,
    EMAIL VARCHAR(50) UNIQUE NOT NULL,
    ROLES VARCHAR [] NOT NULL,
    ACTIVE BOOLEAN DEFAULT true,
    REFRESH_TOKEN VARCHAR(500) DEFAULT NULL,
    CREATED_ON TIMESTAMP DEFAULT NULL,
    LAST_MODIFIED_ON TIMESTAMP DEFAULT NULL
);

INSERT INTO USER_ENTITY (FIRST_NAME, LAST_NAME, PASSWORD, ADDRESS, EMAIL, ROLES)
VALUES ('saiful','rasel', 'yUIixNNy9y/I3Y3goS4IDOtklaJmm8QNAUv2cpwXOE8=', 'Bangladesh', 'abc.test@abc.com', '{"ROLE_USER"}');

--DROP TABLE IF EXISTS CUSTOMER_ENTITY;
CREATE TABLE IF NOT EXISTS CUSTOMER_ENTITY (
    ID VARCHAR(255) NOT NULL PRIMARY KEY,
    EMAIL VARCHAR(255) NOT NULL,
    NAME VARCHAR(255) NOT NULL,
    PHONE VARCHAR(255) DEFAULT NULL,
    CREATED_ON TIMESTAMP DEFAULT NULL,
    LAST_MODIFIED_ON TIMESTAMP DEFAULT NULL
);

--DROP TABLE IF EXISTS PAYMENT_METHOD_ENTITY;
CREATE TABLE IF NOT EXISTS PAYMENT_METHOD_ENTITY (
    ID VARCHAR(255) NOT NULL PRIMARY KEY,
    CUSTOMER_ID VARCHAR(255) NOT NULL,
    CREATED_ON TIMESTAMP DEFAULT NULL,
    LAST_MODIFIED_ON TIMESTAMP DEFAULT NULL
);

--DROP TABLE IF EXISTS PAYMENT_INTENT_ENTITY;
CREATE TABLE IF NOT EXISTS PAYMENT_INTENT_ENTITY (
    ID VARCHAR(255) NOT NULL PRIMARY KEY,
    AMOUNT INTEGER DEFAULT NULL,
    PAYMENT_METHOD_ID VARCHAR(255) DEFAULT NULL,
    CURRENCY VARCHAR(255) NOT NULL,
    STATUS VARCHAR(255) DEFAULT NULL,
    CUSTOMER_ID VARCHAR(255) NOT NULL,
    CREATED_ON TIMESTAMP DEFAULT NULL,
    LAST_MODIFIED_ON TIMESTAMP DEFAULT NULL
);

--DROP TABLE IF EXISTS PRODUCT_ENTITY;
CREATE TABLE IF NOT EXISTS PRODUCT_ENTITY (
    ID VARCHAR(255) NOT NULL PRIMARY KEY,
    NAME VARCHAR(255) DEFAULT NULL,
    DESCRIPTION VARCHAR(255) DEFAULT NULL,
    DEFAULT_PRICE_ID VARCHAR(255) DEFAULT NULL,
    ACTIVE BOOLEAN DEFAULT NULL,
    SHIPPABLE BOOLEAN DEFAULT NULL,
    CREATED_ON TIMESTAMP DEFAULT NULL,
    LAST_MODIFIED_ON TIMESTAMP DEFAULT NULL
);

--DROP TABLE IF EXISTS PRODUCT_PRICE_ENTITY;
CREATE TABLE IF NOT EXISTS PRODUCT_PRICE_ENTITY (
    ID VARCHAR(255) NOT NULL PRIMARY KEY,
    NICK_NAME VARCHAR(255) DEFAULT NULL,
    TYPE VARCHAR(255) NOT NULL,
    CURRENCY VARCHAR(255) DEFAULT NULL,
    PRODUCT_ID VARCHAR(255) NOT NULL,
    ACTIVE BOOLEAN DEFAULT NULL,
    UNIT_AMOUNT INTEGER NOT NULL,
    CREATED_ON TIMESTAMP DEFAULT NULL,
    LAST_MODIFIED_ON TIMESTAMP DEFAULT NULL
);

--DROP TABLE IF EXISTS SUBSCRIPTION_ENTITY;
CREATE TABLE IF NOT EXISTS SUBSCRIPTION_ENTITY (
    ID VARCHAR(255) NOT NULL PRIMARY KEY,
    DESCRIPTION VARCHAR(255) DEFAULT NULL,
    STATUS VARCHAR(255) NOT NULL,
    CURRENCY VARCHAR(255) DEFAULT NULL,
    CUSTOMER_ID VARCHAR(255) NOT NULL,
    CANCEL_AT INTEGER DEFAULT NULL,
    CURRENT_PERIOD_END INTEGER DEFAULT NULL,
    CURRENT_PERIOD_START INTEGER DEFAULT NULL,
    CREATED_ON TIMESTAMP DEFAULT NULL,
    LAST_MODIFIED_ON TIMESTAMP DEFAULT NULL
);

--DROP TABLE IF EXISTS TRANSACTION_ENTITY;
CREATE TABLE IF NOT EXISTS TRANSACTION_ENTITY (
    ID UUID NOT NULL PRIMARY KEY,
    AMOUNT INTEGER DEFAULT NULL,
    CUSTOMER_ID VARCHAR(255) NOT NULL,
    PAYMENT_INTENT_ID VARCHAR(255) DEFAULT NULL,
    IS_SUCCESS BOOLEAN NOT NULL,
    CREATED_ON TIMESTAMP DEFAULT NULL,
    LAST_MODIFIED_ON TIMESTAMP DEFAULT NULL
);