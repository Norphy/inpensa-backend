
-- USER ID: 6b246024-59b1-4716-b583-9a0c4d0e5191
-- USER ID TWO: 6b246024-59b1-4716-b583-9a0c4d0e5111
-- CATEGORY ID: 6b246024-59b1-4716-b583-9a0c4d0e5192
-- SUB CATEGORY ID: 6b246024-59b1-4716-b583-9a0c4d0e5193
-- WALLET ID: 6b246024-59b1-4716-b583-9a0c4d0e5194
-- TRANSACTION ID: 6b246024-59b1-4716-b583-9a0c4d0e5195
-- TRANSACTION ID TWO: 6b246024-59b1-4716-b583-9a0c4d0e5196

INSERT INTO T_USER (ID, EMAIL) VALUES ('6b246024-59b1-4716-b583-9a0c4d0e5191', 'admin@admin.admin');
INSERT INTO T_USER (ID, EMAIL) VALUES ('6b246024-59b1-4716-b583-9a0c4d0e5111', 'superadmin@admin.admin');
--CREATE TABLE T_USER (ID uuid DEFAULT uuid() PRIMARY KEY, EMAIL varchar(50) UNIQUE, PASSWORD varchar(20) NOT NULL);

INSERT INTO T_CATEGORY (ID, CATEGORY, OWNER_ID) VALUES
 ('6b246024-59b1-4716-b583-9a0c4d0e5192', 'Home', '6b246024-59b1-4716-b583-9a0c4d0e5191');
-- T_CATEGORY TABLE --
--CREATE TABLE T_CATEGORY
--(ID UUID DEFAULT uuid() PRIMARY KEY,
--CATEGORY VARCHAR(20) NOT NULL,
--OWNER_ID VARCHAR(50) NOT NULL,
--FOREIGN KEY(OWNER_ID) REFERENCES T_USER(ID));


INSERT INTO T_SUB_CATEGORY (ID, SUB_CATEGORY, CATEGORY_ID, OWNER_ID) VALUES
 ('6b246024-59b1-4716-b583-9a0c4d0e5193', 'Groceries', '6b246024-59b1-4716-b583-9a0c4d0e5192',
  '6b246024-59b1-4716-b583-9a0c4d0e5191');
-- T_SUB_CATEGORY TABLE --
--CREATE TABLE T_SUB_CATEGORY
--(ID UUID DEFAULT uuid() PRIMARY KEY,
--SUB_CATEGORY VARCHAR(200) NOT NULL,
--CATEGORY_ID UUID NOT NULL,
--OWNER_ID VARCHAR(50) NOT NULL,
--FOREIGN KEY(OWNER_ID) REFERENCES T_USER(ID));


INSERT INTO T_WALLET (ID, NAME, DATE_CREATED, AMOUNT, OWNER_ID) VALUES
 ('6b246024-59b1-4716-b583-9a0c4d0e5194', 'Main Wallet',1736160093269, 500,
  '6b246024-59b1-4716-b583-9a0c4d0e5191');
-- T_WALLET TABLE --
--CREATE TABLE T_WALLET
--(ID UUID DEFAULT uuid() PRIMARY KEY,
--NAME VARCHAR(50),
--DATE_CREATED BIGINT NOT NULL,
--AMOUNT BIGINT DEFAULT 0,
--OWNER_ID VARCHAR(50) NOT NULL,
--FOREIGN KEY(OWNER_ID) REFERENCES T_USER(ID));


INSERT INTO T_TRANSACTION
(ID, DATE_CREATED, OCCURRENCE_DATE, DESCRIPTION, AMOUNT, TRANSACTION_TYPE, TAG, CATEGORY_ID, SUB_CATEGORY_ID, WALLET_ID, OWNER_ID)
VALUES ('6b246024-59b1-4716-b583-9a0c4d0e5195', 1736154492435, 1736154492435, 'Description', 56, 'EXPENSE', 'TAG',
 '6b246024-59b1-4716-b583-9a0c4d0e5192', '6b246024-59b1-4716-b583-9a0c4d0e5193', '6b246024-59b1-4716-b583-9a0c4d0e5194', '6b246024-59b1-4716-b583-9a0c4d0e5191');

INSERT INTO T_TRANSACTION
(ID, DATE_CREATED, OCCURRENCE_DATE, DESCRIPTION, AMOUNT, TRANSACTION_TYPE, TAG, CATEGORY_ID, SUB_CATEGORY_ID, WALLET_ID, OWNER_ID)
VALUES ('6b246024-59b1-4716-b583-9a0c4d0e5196', 1736154492435, 1736154492435, 'Description', 56, 'EXPENSE', 'TAG',
 '6b246024-59b1-4716-b583-9a0c4d0e5192', '6b246024-59b1-4716-b583-9a0c4d0e5193', '6b246024-59b1-4716-b583-9a0c4d0e5194', '6b246024-59b1-4716-b583-9a0c4d0e5111');
--CREATE TABLE T_TRANSACTION
--(ID UUID DEFAULT uuid() PRIMARY KEY,
--DATE_CREATED BIGINT NOT NULL,
--OCCURRENCE_DATE BIGINT NOT NULL,
--DESCRIPTION VARCHAR(256) NOT NULL,
--AMOUNT INT NOT NULL,
--TRANSACTION_TYPE VARCHAR(15) NOT NULL,
--TAG VARCHAR(30) NOT NULL,
--CATEGORY_ID UUID NOT NULL,
--SUB_CATEGORY_ID UUID NOT NULL,
--WALLET_ID UUID NOT NULL,
--OWNER_ID VARCHAR(50) NOT NULL,
--FOREIGN KEY(WALLET_ID) REFERENCES T_WALLET(ID),
--FOREIGN KEY(OWNER_ID) REFERENCES T_USER (ID),
--FOREIGN KEY(CATEGORY_ID) REFERENCES T_CATEGORY (ID),
--FOREIGN KEY(SUB_CATEGORY_ID) REFERENCES T_SUB_CATEGORY (ID));
