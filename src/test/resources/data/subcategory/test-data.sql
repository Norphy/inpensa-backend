
-- USER ID: 6b246024-59b1-4716-b583-9a0c4d0e5191
-- USER ID TWO: 6b246024-59b1-4716-b583-9a0c4d0e5111
-- CATEGORY ID: 6b246024-59b1-4716-b583-9a0c4d0e5192
-- CATEGORY ID TWO: 6b246024-59b1-4716-b583-9a0c4d0e5112
-- SUB_CATEGORY ID: 6b246024-59b1-4716-b583-9a0c4d0e5193
-- SUB_CATEGORY ID TWO: 6b246024-59b1-4716-b583-9a0c4d0e5113

INSERT INTO T_USER (ID, EMAIL) VALUES ('6b246024-59b1-4716-b583-9a0c4d0e5191', 'admin@admin.admin');
INSERT INTO T_USER (ID, EMAIL) VALUES ('6b246024-59b1-4716-b583-9a0c4d0e5111', 'superadmin@admin.admin');
--CREATE TABLE T_USER (ID uuid DEFAULT uuid() PRIMARY KEY, EMAIL varchar(50) UNIQUE, PASSWORD varchar(20) NOT NULL);

INSERT INTO T_CATEGORY (ID, CATEGORY, OWNER_ID) VALUES
 ('6b246024-59b1-4716-b583-9a0c4d0e5192', 'Home', '6b246024-59b1-4716-b583-9a0c4d0e5191');
INSERT INTO T_CATEGORY (ID, CATEGORY, OWNER_ID) VALUES
 ('6b246024-59b1-4716-b583-9a0c4d0e5112', 'Food', '6b246024-59b1-4716-b583-9a0c4d0e5111');
-- T_CATEGORY TABLE --
--CREATE TABLE T_CATEGORY
--(ID UUID DEFAULT uuid() PRIMARY KEY,
--CATEGORY VARCHAR(20) NOT NULL,
--OWNER_ID VARCHAR(50) NOT NULL,
--FOREIGN KEY(OWNER_ID) REFERENCES T_USER(ID));


INSERT INTO T_SUB_CATEGORY (ID, SUB_CATEGORY, CATEGORY_ID, OWNER_ID) VALUES
 ('6b246024-59b1-4716-b583-9a0c4d0e5193', 'Cleaning', '6b246024-59b1-4716-b583-9a0c4d0e5192',
  '6b246024-59b1-4716-b583-9a0c4d0e5191');
INSERT INTO T_SUB_CATEGORY (ID, SUB_CATEGORY, CATEGORY_ID, OWNER_ID) VALUES
 ('6b246024-59b1-4716-b583-9a0c4d0e5113', 'Restaurants', '6b246024-59b1-4716-b583-9a0c4d0e5112',
  '6b246024-59b1-4716-b583-9a0c4d0e5111');
-- T_SUB_CATEGORY TABLE --
--CREATE TABLE T_SUB_CATEGORY
--(ID UUID DEFAULT uuid() PRIMARY KEY,
--SUB_CATEGORY VARCHAR(200) NOT NULL,
--CATEGORY_ID UUID NOT NULL,
--OWNER_ID VARCHAR(50) NOT NULL,
--FOREIGN KEY(OWNER_ID) REFERENCES T_USER(ID));