-- liquibase formatted sql

-- changeset 0tae:changelog-user-1.0
CREATE TABLE user (
                      id INT PRIMARY KEY AUTO_INCREMENT,
                      email VARCHAR(255) NOT NULL UNIQUE  ,
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
                      refresh_token TEXT
--                         INDEX (email)
);

-- changeset 0tae:changelog-login_challenge-1.0
CREATE TABLE login_challenge (
                                 id INT PRIMARY KEY,
                                 user_id INT,
                                 is_successful BOOLEAN DEFAULT FALSE,
                                 created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                 FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE
);

-- changeset 0tae:changelog-signup_code_challenge-1.0
CREATE TABLE signup_code_challenge (
                                       id INT PRIMARY KEY,
                                       user_id INT,
                                       code VARCHAR(255) NOT NULL,
                                       is_successful BOOLEAN DEFAULT FALSE,
                                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                       verified_at TIMESTAMP,
                                       FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE
);

-- changeset 0tae:changelog-car_manufacturer-1.0
CREATE TABLE car_manufacturer (
                                  id INT PRIMARY KEY,
                                  name VARCHAR(255) NOT NULL,
                                  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                  deleted_at TIMESTAMP
);


-- changeset 0tae:changelog-car-1.0
CREATE TABLE car (
                     id INT PRIMARY KEY,
                     user_id INT,
                     name VARCHAR(255) NOT NULL,
                     car_manufacturer_id INT,
                     bought_at TIMESTAMP,
                     created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                     deleted_at TIMESTAMP,
                     FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
                     FOREIGN KEY (car_manufacturer_id) REFERENCES car_manufacturer(id) ON DELETE SET NULL
);

-- changeset 0tae:changelog-car_description-1.0
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

-- changeset 0tae:changelog-repair_shop-1.0
CREATE TABLE repair_shop (
                             id INT PRIMARY KEY,
                             name VARCHAR(255) NOT NULL,
                             location VARCHAR(255),
                             created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                             deleted_at TIMESTAMP
);


-- changeset 0tae:changelog-repair_man-1.0
CREATE TABLE repair_man (
                            id INT PRIMARY KEY,
                            name VARCHAR(255) NOT NULL,
                            license_id INT,
                            career_start_at TIMESTAMP,
                            created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            deleted_at TIMESTAMP
);

-- changeset 0tae:changelog-maintenance_item-1.0
CREATE TABLE maintenance_item (
                                  id INT PRIMARY KEY,
                                  name VARCHAR(255) NOT NULL,
                                  required_license INT CHECK ( required_license IN (1,2,3) ) NOT NULL,
                                  required_time INT NOT NULL
);

-- changeset 0tae:changelog-reserve-1.0
CREATE TABLE reserve (
                         id INT PRIMARY KEY,
                         start_time TIMESTAMP NOT NULL,
                         end_time TIMESTAMP NOT NULL,
                         car_id INT NOT NULL,
                         repair_man_id INT NOT NULL,
                         repair_shop_id INT NOT NULL,
                         maintenance_item_id INT NOT NULL,
                         created_at TIMESTAMP NOT NULL,
                         deleted_at TIMESTAMP NOT NULL,
                         FOREIGN KEY (car_id) REFERENCES car(id),
                         FOREIGN KEY (repair_man_id) REFERENCES repair_man(id),
                         FOREIGN KEY (repair_shop_id) REFERENCES repair_shop(id),
                         FOREIGN KEY (maintenance_item_id) REFERENCES maintenance_item(id)
);


-- changeset 0tae:changelog-maintenance_item-1.1
ALTER TABLE maintenance_item ADD CONSTRAINT check_require_time
    CHECK (required_time BETWEEN 30 AND 300);


-- changeset 0tae:changelog-user-1.1
ALTER TABLE user ADD INDEX user_email_index (email);

-- changeset 0tae:changelog-sample.1.0
INSERT INTO user (id, email, name, phone_number, password) VALUES
                                                               (1, 'john@example.com', 'John Doe', '1234567890', 'password1'),
                                                               (2, 'jane@example.com', 'Jane Doe', '0987654321', 'password2');

INSERT INTO login_challenge (id, user_id, is_successful) VALUES
                                                             (1, 1, TRUE),
                                                             (2, 2, TRUE);

INSERT INTO signup_code_challenge (id, user_id, code, is_successful) VALUES
                                                                         (1, 1, 'code1', TRUE),
                                                                         (2, 2, 'code2', TRUE);

INSERT INTO car_manufacturer (id, name) VALUES
                                            (1, 'Manufacturer A'),
                                            (2, 'Manufacturer B');

INSERT INTO car (id, user_id, name, car_manufacturer_id) VALUES
                                                             (1, 1, 'Car 1', 1),
                                                             (2, 2, 'Car 2', 2);

INSERT INTO car_description (id, car_id, color, seats) VALUES
                                                           (1, 1, 'Red', 4),
                                                           (2, 2, 'Blue', 4);

INSERT INTO repair_shop (id, name, location) VALUES
                                                 (1, 'Shop A', 'Location A'),
                                                 (2, 'Shop B', 'Location B');

INSERT INTO repair_man (id, name, license_id) VALUES
                                                  (1, 'Repairman A', 1),
                                                  (2, 'Repairman B', 2);

SELECT * FROM user;

-- changeset 0tae:changelog-reserve.1.1
ALTER TABLE reserve MODIFY id INT NOT NULL AUTO_INCREMENT;

-- changeset 0tae:changelog-login_challenge.1.1
ALTER TABLE login_challenge MODIFY id INT NOT NULL AUTO_INCREMENT;

-- changeset 0tae:changelog-signup_code_challenge.1.1
ALTER TABLE signup_code_challenge MODIFY id INT NOT NULL AUTO_INCREMENT;