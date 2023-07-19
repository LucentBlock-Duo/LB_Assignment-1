-- liquibase formatted sql logicalFilePath:assignment2/changelog/mysql/v1/v.1.4.sql

-- changeset 0tae:repair_shop-3 labels:v1,1.4,location_search_service
-- comment: feat : location 정보 구체화를 위한 road_address, latitude, longitude 추가
ALTER TABLE location ADD COLUMN address VARCHAR(255) NOT NULL;
ALTER TABLE location ADD COLUMN latitude VARCHAR(255) NOT NULL;
ALTER TABLE location ADD COLUMN longitude VARCHAR(255) NOT NULL;
ALTER TABLE location ADD COLUMN post_num INT NOT NULL;


-- changeset 0tae:repair_shop-4 labels:v1,1.4,location_search_service
-- comment: feat : location 정보 구체화를 위한 road_address 추가
ALTER TABLE location ADD COLUMN road_address VARCHAR(255) NOT NULL;

-- changeset 0tae:repair_shop-5 labels:v1,1.4,location_search_service
-- comment: fix : location 과 repair_shop을 병합
ALTER TABLE repair_shop DROP CONSTRAINT repair_shop_ibfk_1;
ALTER TABLE repair_shop DROP location_id;
ALTER TABLE repair_shop ADD COLUMN road_address VARCHAR(255) NOT NULL;
ALTER TABLE repair_shop ADD COLUMN address VARCHAR(255) NOT NULL;
ALTER TABLE repair_shop ADD COLUMN latitude VARCHAR(255) NOT NULL;
ALTER TABLE repair_shop ADD COLUMN longitude VARCHAR(255) NOT NULL;
ALTER TABLE repair_shop ADD COLUMN post_num INT NOT NULL;
ALTER TABLE repair_shop ADD COLUMN province VARCHAR(25) NOT NULL;
ALTER TABLE repair_shop ADD COLUMN city VARCHAR(10) NOT NULL;

-- changeset 0tae:user-3 labels:v1,1.4,location_search_service,gps_service
-- comment: fix : user의 latitude, longitude 추가
ALTER TABLE user ADD COLUMN latitude VARCHAR(255) NOT NULL;
ALTER TABLE user ADD COLUMN longitude VARCHAR(255) NOT NULL;
ALTER TABLE user ADD COLUMN gps_authorized BOOLEAN DEFAULT false;

-- changeset 0tae:user-4 labels:v1,1.4,location_search_service,gps_service
-- comment: fix : user 수정
ALTER TABLE user MODIFY COLUMN latitude VARCHAR(255);
ALTER TABLE user MODIFY COLUMN longitude VARCHAR(255);