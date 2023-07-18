-- liquibase formatted sql logicalFilePath:assignment2/changelog/mysql/v1/v.1.3.sql


-- changeset 0tae:user-2 labels:v2,1.3,preference
-- comment: feat : user 잔액 컬럼 추가
ALTER TABLE user
    ADD COLUMN balance INT DEFAULT 0;

-- changeset 0tae:repair_man_car_manufacturer_preference-1 labels:v1,1.3,preference
-- comment: feat : 테이블 생성 및 제약조건 추가
CREATE TABLE repair_man_preference
(
    id                  INT PRIMARY KEY AUTO_INCREMENT,
    repair_man_id       INT       NOT NULL,
    car_manufacturer_id INT       NOT NULL,
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at          TIMESTAMP,
    FOREIGN KEY (repair_man_id) references repair_man (id),
    FOREIGN KEY (car_manufacturer_id) references car_manufacturer (id)
);

-- changeset 0tae:repair_man_maintenance_item_preference-1 labels:v1,1.0,preference
-- comment: feat : 테이블 생성 및 제약조건 추가
CREATE TABLE repair_man_maintenance_item_preference
(
    id                  INT PRIMARY KEY AUTO_INCREMENT,
    repair_man_id       INT       NOT NULL,
    maintenance_item_id INT       NOT NULL,
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at          TIMESTAMP,
    FOREIGN KEY (repair_man_id) references repair_man (id),
    FOREIGN KEY (maintenance_item_id) references maintenance_item (id)
);

-- changeset 0tae:user_car_manufacturer_preference-1 labels:v1,1.0,preference
-- comment: feat : 테이블 생성 및 제약조건 추가
CREATE TABLE user_car_manufacturer_preference
(
    id                  INT PRIMARY KEY AUTO_INCREMENT,
    user_id             INT       NOT NULL,
    car_manufacturer_id INT       NOT NULL,
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at          TIMESTAMP,
    FOREIGN KEY (user_id) references user (id),
    FOREIGN KEY (car_manufacturer_id) references car_manufacturer (id)
);

-- changeset 0tae:user_maintenance_item_preference-1 labels:v1,1.0,preference
-- comment: feat : 테이블 생성 및 제약조건 추가
CREATE TABLE user_maintenance_item_preference
(
    id                  INT PRIMARY KEY AUTO_INCREMENT,
    user_id             INT       NOT NULL,
    maintenance_item_id INT       NOT NULL,
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at          TIMESTAMP,
    FOREIGN KEY (user_id) references user (id),
    FOREIGN KEY (maintenance_item_id) references maintenance_item (id)
);

-- changeset 0tae:item_detail-1 labels:v1,1.0,preference
-- comment: feat : 테이블 생성 및 제약조건 추가
CREATE TABLE item_detail
(
    id                  INT PRIMARY KEY AUTO_INCREMENT,
    maintenance_item_id INT       NOT NULL,
    repair_man_id       INT       NOT NULL,
    price               INT       NOT NULL,
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at          TIMESTAMP,
    FOREIGN KEY (maintenance_item_id) references maintenance_item (id),
    FOREIGN KEY (repair_man_id) references repair_man (id)
);
