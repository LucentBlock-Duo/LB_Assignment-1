-- liquibase formatted sql logicalFilePath:assignment2/changelog/mysql/v1/v.1.1.sql

-- changeset 0tae:maintenance_item-v.1.1 labels:v1,1.1
-- comment: fix : 누락 데이터 추가
ALTER TABLE maintenance_item ADD COLUMN created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE maintenance_item ADD COLUMN deleted_at TIMESTAMP;

-- changeset 0tae:car-v.1.1 labels:v1,1.1
-- comment: fix : 번호판 column 추가
ALTER TABLE car ADD COLUMN license_plate_no VARCHAR(25) NOT NULL;