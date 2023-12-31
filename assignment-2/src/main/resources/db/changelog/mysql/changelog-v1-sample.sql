-- liquibase formatted sql logicalFilePath:assignment2/changelog/mysql/v1/v.1.0_sample.sql

-- changeset 0tae:sample-v.1.0 labels:v1,1.0 runOnChange:true
-- comment: init : 초기 샘플 데이터 추가

DELETE from reserve;
DELETE from repair_man;
DELETE from car_description;
DELETE from maintenance_item;
DELETE from car;
DELETE from car_manufacturer;
DELETE from signup_code_challenge;
DELETE from login_challenge;
DELETE from user;

INSERT INTO user
    (id, email, name, phone_number, password, password_fail_count, is_email_verified, recent_login_at, created_at, deleted_at, role, provider, provider_id, refresh_token, balance, gps_authorized, longitude, latitude)
    VALUES (1, 'ilmo@gmail.com', 'moil', '01012345678', '$2a$10$TbR1IsZ0aRXlLSsNxQu1R.ZGtoTEpnt4nSRDN/2JXpo84tIzGtCEC', 0, false, null, '2023-07-05 12:53:45', null, 'ROLE_USER', null, null, null, 10000, true, 127, 37);
INSERT INTO user
    (id, email, name, phone_number, password, password_fail_count, is_email_verified, recent_login_at, created_at, deleted_at, role, provider, provider_id, refresh_token, balance, gps_authorized, longitude, latitude)
    VALUES (2, 'choi@gmail.com', 'choi', '01012345678', '$2a$10$4YhdTqcJf6oWcLX46lW6/uFJX7JGOHJVFJ30DRm6bJfFRubw/Hfka', 0, false, null, '2023-07-05 12:53:45', null, 'ROLE_USER', null, null, null, 10000, true, 127, 37);

INSERT INTO car_manufacturer (id, name) VALUES
                                            (1, 'HYUNDAI'),
                                            (2, 'KIA'),
                                            (3, 'MERCEDES BENZ'),
                                            (4, 'BMW'),
                                            (5, 'AUDI'),
                                            (6, 'CHEVROLET'),
                                            (7, 'RENAULT');

INSERT INTO car (id,license_plate_no, user_id, name, car_manufacturer_id, bought_at, created_at, deleted_at)
VALUES
    (1, '987가6543',1, 'ELANTRA', 1, TIMESTAMP('2023-07-05 10:00:00'), TIMESTAMP('2023-07-04 10:00:00'), null),
    (2, '876가5432',2, 'S580', 3, TIMESTAMP('2023-07-06 00:00:00'), TIMESTAMP('2023-07-05 10:00:00'), null);

INSERT INTO maintenance_item (id, name, required_license, required_time, created_at, deleted_at) VALUES
                                                                                                     (1, '엔진오일 교환', 1, 30, TIMESTAMP(NOW()), null),
                                                                                                     (2, '판금도색', 2, 40, TIMESTAMP(NOW()), null),
                                                                                                     (3, '파워트레인 교체', 3, 60, TIMESTAMP(now()), null),
                                                                                                     (4, '타이어 교환', 3, 60, TIMESTAMP(now()), null),
                                                                                                     (5, '차량용 소프트웨어 업데이트', 3, 60, TIMESTAMP(now()), null);

INSERT INTO car_description (id, car_id, color, seats) VALUES
                                                           (1, 1, 'Red', 4),
                                                           (2, 2, 'Blue', 4);


INSERT INTO repair_man (id, name, license_id, evaluated_num, evaluation_grade) VALUES
                                                  (1, 'Repairman A', 1, 10, 4.0),
                                                  (2, 'Repairman B', 2, 11, 4.0),
                                                  (3, 'Repairman C', 3, 12, 4.2);

INSERT INTO item_detail (id, maintenance_item_id, repair_man_id, price, created_at, deleted_at) VALUES
                                                                                                    (1, 1, 1, 4000, TIMESTAMP(now()), null),
                                                                                                    (2, 1, 2, 3000, TIMESTAMP(now()), null),
                                                                                                    (3, 2, 1, 5000, TIMESTAMP(now()), null),
                                                                                                    (4, 2, 3, 6000, TIMESTAMP(now()), null),
                                                                                                    (5, 3, 2, 10000, TIMESTAMP(now()), null);

INSERT INTO repair_man_brand_preference (id, repair_man_id, car_manufacturer_id, created_at, deleted_at) VALUES
                                                                                                                        (1, 1, 1, TIMESTAMP(now()), null),
                                                                                                                        (2, 2, 2, TIMESTAMP(now()), null),
                                                                                                                        (3, 3, 3, TIMESTAMP(now()), null),
                                                                                                                        (4, 1, 4, TIMESTAMP(now()), null),
                                                                                                                        (5, 2, 5, TIMESTAMP(now()), null),
                                                                                                                        (6, 3, 6, TIMESTAMP(now()), null);

INSERT INTO user_brand_preference (id, user_id, car_manufacturer_id, created_at, deleted_at) VALUES
                                                                                                            (1, 1, 1, TIMESTAMP(now()), null),
                                                                                                            (2, 2, 2, TIMESTAMP(now()), null),
                                                                                                            (3, 1, 3, TIMESTAMP(now()), null),
                                                                                                            (4, 2, 4, TIMESTAMP(now()), null);

INSERT INTO repair_man_item_preference (id, repair_man_id, maintenance_item_id, created_at, deleted_at) VALUES
                                                                                                                        (1, 1, 1, TIMESTAMP(now()), null),
                                                                                                                        (2, 2, 2, TIMESTAMP(now()), null),
                                                                                                                        (3, 3, 3, TIMESTAMP(now()), null),
                                                                                                                        (4, 1, 4, TIMESTAMP(now()), null),
                                                                                                                        (5, 2, 5, TIMESTAMP(now()), null);

INSERT INTO user_item_preference (id, user_id, maintenance_item_id, created_at, deleted_at) VALUES
                                                                                                            (1, 1, 1, TIMESTAMP(now()), null),
                                                                                                            (2, 2, 2, TIMESTAMP(now()), null),
                                                                                                            (3, 1, 3, TIMESTAMP(now()), null),
                                                                                                            (4, 2, 4, TIMESTAMP(now()), null);
