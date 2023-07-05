-- liquibase formatted sql logicalFilePath:assignment2/changelog/mysql/v1/v.1.0_sample.sql

-- changeset 0tae:sample-v.1.0 labels:v1,1.0
-- comment: init : 초기 샘플 데이터 추가
INSERT INTO assignment.user
    (id, email, name, phone_number, password, password_fail_count, is_email_verified, recent_login_at, created_at, deleted_at, role, provider, provider_id, refresh_token)
    VALUES (3, 'ilmo@gmail.com', 'moil', '01012345678', '$2a$10$TbR1IsZ0aRXlLSsNxQu1R.ZGtoTEpnt4nSRDN/2JXpo84tIzGtCEC', 0, null, '2023-07-05 12:55:59', '2023-07-05 12:53:45', null, 'ROLE_ADMIN', null, null, 'eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiUk9MRV9VU0VSIiwic3ViIjoiaWxtb0BnbWFpbC5jb20iLCJpYXQiOjE2ODg1MjkzNTksImV4cCI6MTY4OTczODk1OX0.FcHp8VrXSKPWm95oOD8uUGhP-X_BP4iaU_j-Y_x5NaM');
INSERT INTO assignment.user
    (id, email, name, phone_number, password, password_fail_count, is_email_verified, recent_login_at, created_at, deleted_at, role, provider, provider_id, refresh_token)
    VALUES (4, 'choi@gmail.com', 'choi', '01012345678', '$2a$10$4YhdTqcJf6oWcLX46lW6/uFJX7JGOHJVFJ30DRm6bJfFRubw/Hfka', 0, null, null, '2023-07-05 12:53:45', null, 'ROLE_ADMIN', null, null, null);

INSERT INTO login_challenge (id, user_id, is_successful) VALUES
                                                             (1, 3, TRUE),
                                                             (2, 4, TRUE);

INSERT INTO signup_code_challenge (id, user_id, code, is_successful) VALUES
                                                                         (1, 3, 'code1', TRUE),
                                                                         (2, 4, 'code2', TRUE);

INSERT INTO car_manufacturer (id, name) VALUES
                                            (3, 'HYUNDAI'),
                                            (4, 'KIA'),
                                            (5, 'MERCEDES BENZ'),
                                            (6, 'BMW'),
                                            (7, 'AUDI'),
                                            (8, 'CHEVROLET'),
                                            (9, 'RENAULT');

INSERT INTO car (id, user_id, name, car_manufacturer_id, bought_at, created_at, deleted_at) VALUES
                                                                                                (3, 3, 'ELANTRA', 3, TIMESTAMP('2023-07-05 10:00:00'), TIMESTAMP('2023-07-04 10:00:00'), null),
                                                                                                (5, 4, 'S580', 5, TIMESTAMP('2023-07-06 00:00:00'), TIMESTAMP('2023-07-05 10:00:00'), null);

INSERT INTO maintenance_item (id, name, required_license, required_time, created_at, deleted_at) VALUES
                                                                                                     (7, '엔진 오일', 1, 30, TIMESTAMP(NOW()), null),
                                                                                                     (8, '타이어 교환', 2, 40, TIMESTAMP(NOW()), null),
                                                                                                     (9, '범퍼 교체', 3, 60, TIMESTAMP(now()), null);

INSERT INTO car_description (id, car_id, color, seats) VALUES
                                                           (1, 3, 'Red', 4),
                                                           (2, 5, 'Blue', 4);

INSERT INTO repair_shop (id, name, location) VALUES
                                                 (1, 'Shop A', 'Location A'),
                                                 (2, 'Shop B', 'Location B');

INSERT INTO repair_man (id, name, license_id) VALUES
                                                  (1, 'Repairman A', 1),
                                                  (2, 'Repairman B', 2),
                                                  (3, 'Repairman C', 3);