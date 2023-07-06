-- liquibase formatted sql logicalFilePath:assignment2/changelog/mysql/v1/v.1.1.sql

-- changeset 0tae:maintenance_item-v.1.1 labels:v1,1.1
-- comment: fix : 누락 데이터 추가
ALTER TABLE maintenance_item ADD COLUMN created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE maintenance_item ADD COLUMN deleted_at TIMESTAMP;

-- changeset 0tae:car-v.1.1 labels:v1,1.1
-- comment: fix : 번호판 column 추가
ALTER TABLE car ADD COLUMN license_plate_no VARCHAR(25) NOT NULL;

-- changeset 0tae:location-v.1.1 labels:v1,1.1,create
-- comment: init : 테이블 생성 및 제약조건 추가
CREATE TABLE location (
                          id INT PRIMARY KEY AUTO_INCREMENT,
                          province VARCHAR(25) NOT NULL ,
                          city VARCHAR(10) NOT NULL ,
                          created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          deleted_at TIMESTAMP
);

-- changeset 0tae:repair_shop-v.1.1 labels:v1,1.1,location
-- comment: fix : location column 삭제 후 location_id 추가 및 외래키로 지정
ALTER TABLE repair_shop DROP location;
ALTER TABLE repair_shop ADD COLUMN location_id INT;
ALTER TABLE repair_shop ADD FOREIGN KEY(location_id) REFERENCES location(id);
