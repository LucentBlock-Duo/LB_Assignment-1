-- liquibase formatted sql logicalFilePath:assignment2/changelog/mysql/v1/v.1.0.sql

/* @runOnChange = true 를 통해 changeset 에 대한 직접적인 수정사항 발생 시 일어나는 MD5 체크섬 오류를 Disable 할 수 있음.
   이는 언제든지 바뀔 수 있다는 것을 의미
   @runTransaction = true 를 통해 해당 changeset 을 transaction 으로 실행
   @runAlways = true 를 통해 이전에 실행된 적이 있더라도 해당 changeset 을 실행함, 원래는 같은 ID 일 때 해당 작업을 건너 뛰었음 */

-- changeset 0tae:user-1 labels:v1,1.0,init
-- comment: init : 테이블 생성 및 제약조건 추가
CREATE TABLE user (
                      id INT PRIMARY KEY AUTO_INCREMENT,
                      email VARCHAR(255) NOT NULL,
                      name VARCHAR(255) NOT NULL,
                      phone_number VARCHAR(20),
                      password VARCHAR(255) NOT NULL,
                      password_fail_count INT DEFAULT 0,
                      is_email_verified BOOLEAN DEFAULT FALSE,
                      recent_login_at TIMESTAMP,
                      created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                      deleted_at TIMESTAMP,
                      role VARCHAR(255) CHECK ( role in ('ROLE_USER', 'ROLE_ADMIN') ),
                      provider VARCHAR(255),
                      provider_id VARCHAR(255),
                      refresh_token TEXT,
                      balance INT DEFAULT 0
);

-- changeset 0tae:car_manufacturer-1 labels:v1,1.0,init
-- comment: init : 테이블 생성 및 제약조건 추가
CREATE TABLE car_manufacturer (
                                  id INT PRIMARY KEY,
                                  name VARCHAR(255) NOT NULL,
                                  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                  deleted_at TIMESTAMP
);

-- changeset 0tae:car-1 labels:v1,1.0,init
-- comment: init : 테이블 생성 및 제약조건 추가
CREATE TABLE car (
                     id INT PRIMARY KEY AUTO_INCREMENT,
                     user_id INT,
                     name VARCHAR(255) NOT NULL,
                     car_manufacturer_id INT,
                     bought_at TIMESTAMP,
                     created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                     deleted_at TIMESTAMP,
                     FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
                     FOREIGN KEY (car_manufacturer_id) REFERENCES car_manufacturer(id) ON DELETE SET NULL
);

-- changeset 0tae:car_description-1 labels:v1,1.0,init
-- comment: init : 테이블 생성 및 제약조건 추가
CREATE TABLE car_description (
                                 id INT PRIMARY KEY,
                                 car_id INT,
                                 color VARCHAR(255),
                                 seats INT,
                                 created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                 deleted_at TIMESTAMP,
                                 FOREIGN KEY (car_id) REFERENCES car(id) ON DELETE CASCADE
--                                    INDEX(car_id)
);

-- changeset 0tae:login_challenge-1 labels:v1,1.0,init
-- comment: init : 테이블 생성 및 제약조건 추가
CREATE TABLE login_challenge (
                                 id INT PRIMARY KEY AUTO_INCREMENT,
                                 user_id INT,
                                 is_successful BOOLEAN DEFAULT FALSE,
                                 created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                 FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE
);

-- changeset 0tae:signup_code_challenge-1 labels:v1,1.0,init
-- comment: init : 테이블 생성 및 제약조건 추가
CREATE TABLE signup_code_challenge (
                                       id INT PRIMARY KEY AUTO_INCREMENT,
                                       user_id INT,
                                       code VARCHAR(255) NOT NULL,
                                       is_successful BOOLEAN DEFAULT FALSE,
                                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                       verified_at TIMESTAMP,
                                       FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE
);

-- changeset 0tae:repair_man-1 labels:v1,1.0,init
-- comment: init : 테이블 생성 및 제약조건 추가
CREATE TABLE repair_man (
                            id INT PRIMARY KEY,
                            name VARCHAR(255) NOT NULL,
                            license_id INT,
                            career_start_at TIMESTAMP,
                            created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            deleted_at TIMESTAMP
);

-- changeset 0tae:repair_shop-1 labels:v1,1.0,init
-- comment: init : 테이블 생성 및 제약조건 추가
CREATE TABLE repair_shop (
                             id INT PRIMARY KEY,
                             name VARCHAR(255) NOT NULL,
                             location VARCHAR(255),
                             created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                             deleted_at TIMESTAMP
);

-- changeset 0tae:maintenance_item-1 labels:v1,1.0,init
-- comment: init : 테이블 생성 및 제약조건 추가
CREATE TABLE maintenance_item (
                                  id INT PRIMARY KEY,
                                  name VARCHAR(255) NOT NULL,
                                  required_license INT CHECK ( required_license IN (1,2,3) ) NOT NULL,
                                  required_time INT CHECK (required_time BETWEEN 30 AND 300) NOT NULL
);

-- changeset 0tae:reserve-1 labels:v1,1.0,init
-- comment: init : 테이블 생성 및 제약조건 추가
CREATE TABLE reserve (
                         id INT PRIMARY KEY AUTO_INCREMENT,
                         start_time TIME NOT NULL,
                         end_time TIME NOT NULL,
                         date DATE NOT NULL,
                         car_id INT NOT NULL,
                         repair_man_id INT NOT NULL,
                         repair_shop_id INT NOT NULL,
                         item_detail_id INT NOT NULL,
                         created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         deleted_at TIMESTAMP,
                         FOREIGN KEY (car_id) REFERENCES car(id),
                         FOREIGN KEY (repair_man_id) REFERENCES repair_man(id),
                         FOREIGN KEY (repair_shop_id) REFERENCES repair_shop(id),
                         FOREIGN KEY (item_detail_id) REFERENCES maintenance_item(id)
);

-- changeset 0tae:repair_man_car_manufacturer_preference-1 labels:v1,1.0,init
-- comment: init : 테이블 생성 및 제약조건 추가
CREATE TABLE repair_man_car_manufacturer_preference (
    id INT PRIMARY KEY AUTO_INCREMENT,
    repair_man_id INT NOT NULL,
    car_manufacturer_id INT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP,
    FOREIGN KEY (repair_man_id) references repair_man(id),
    FOREIGN KEY (car_manufacturer_id) references car_manufacturer(id)
)

-- changeset 0tae:repair_man_maintenance_item_preference-1 labels:v1,1.0,init
-- comment: init : 테이블 생성 및 제약조건 추가
CREATE TABLE repair_man_maintenance_item_preference (
                                       id INT PRIMARY KEY AUTO_INCREMENT,
                                       repair_man_id INT NOT NULL,
                                       maintenance_item_id INT NOT NULL,
                                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                       deleted_at TIMESTAMP,
                                       FOREIGN KEY (repair_man_id) references repair_man(id),
                                       FOREIGN KEY (maintenance_item_id) references maintenance_item(id)
)

-- changeset 0tae:user_car_manufacturer_preference-1 labels:v1,1.0,init
-- comment: init : 테이블 생성 및 제약조건 추가
CREATE TABLE user_car_manufacturer_preference (
                                       id INT PRIMARY KEY AUTO_INCREMENT,
                                       user_id INT NOT NULL,
                                       car_manufacturer_id INT NOT NULL,
                                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                       deleted_at TIMESTAMP,
                                       FOREIGN KEY (user_id) references user(id),
                                       FOREIGN KEY (car_manufacturer_id) references car_manufacturer(id)
)

-- changeset 0tae:user_maintenance_item_preference-1 labels:v1,1.0,init
-- comment: init : 테이블 생성 및 제약조건 추가
CREATE TABLE user_maintenance_item_preference (
                                                  id INT PRIMARY KEY AUTO_INCREMENT,
                                                  user_id INT NOT NULL,
                                                  maintenance_item_id INT NOT NULL,
                                                  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                                  deleted_at TIMESTAMP,
                                                  FOREIGN KEY (user_id) references user(id),
                                                  FOREIGN KEY (maintenance_item_id) references maintenance_item(id)
)

-- changeset 0tae:item_detail-1 labels:v1,1.0,init
-- comment: init : 테이블 생성 및 제약조건 추가
CREATE TABLE item_detail (
    id INT PRIMARY KEY AUTO_INCREMENT,
    maintenance_item_id INT NOT NULL,
    repair_man_id INT NOT NULL,
    price INT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP,
    FOREIGN KEY (maintenance_item_id) references maintenance_item(id),
    FOREIGN KEY (repair_man_id) references repair_man(id)
)


-- changeset 0tae:maintenance_item-2 labels:v1,1.0,fix
-- comment: fix : 누락 데이터 추가
ALTER TABLE maintenance_item ADD COLUMN created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE maintenance_item ADD COLUMN deleted_at TIMESTAMP;

-- changeset 0tae:car-2 labels:v1,1.0,fix
-- comment: fix : 번호판 column 추가
ALTER TABLE car ADD COLUMN license_plate_no VARCHAR(25) NOT NULL;

-- changeset 0tae:reserve_index-1 labels:v1,1.0,fix
-- comment: fix : 누락 데이터 추가
ALTER TABLE reserve ADD INDEX(start_time);
ALTER TABLE reserve ADD INDEX(end_time);