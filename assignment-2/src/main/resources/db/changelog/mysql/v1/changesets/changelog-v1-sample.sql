-- liquibase formatted sql logicalFilePath:assignment2/changelog/mysql/v1/v.1.0_sample.sql

-- changeset 0tae:sample-v.1.0 labels:v1,1.0
-- comment: init : 초기 샘플 데이터 추가
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
                                                  (2, 'Repairman B', 2),
                                                  (3, 'Repairman C', 3);


-- changeset 0tae:sample-v.1.1 labels:v1,1.1
-- comment: fix : main

INSERT INTO maintenance_item (id, name, required_license, required_time)

VALUES (1, 'level1-1',1,30),
       (2, 'level1-2',1,45),
       (3, 'level2-1',2,60),
       (4, 'level2-2',2,120),
       (5, 'level3-1',3,250),
       (6, 'level3-2',3,300);