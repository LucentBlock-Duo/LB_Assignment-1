-- liquibase formatted sql logicalFilePath:assignment2/changelog/mysql/v1/v.1.4.sql

-- changeset 0tae:repair_shop-3 labels:v1,1.4,location_search_service
-- comment: feat : location 정보 구체화를 위한 road_address, latitude, longitude 추가
ALTER TABLE location ADD COLUMN address VARCHAR(255) NOT NULL;
ALTER TABLE location ADD COLUMN latitude VARCHAR(255);
ALTER TABLE location ADD COLUMN longitude VARCHAR(255);