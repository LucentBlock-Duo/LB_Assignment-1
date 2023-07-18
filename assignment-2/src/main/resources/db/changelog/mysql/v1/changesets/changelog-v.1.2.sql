-- liquibase formatted sql logicalFilePath:assignment2/changelog/mysql/v1/v.1.2.sql

-- changeset 0tae:repair_man-1 labels:v1,1.2,repair_man_service
-- comment: feat : evaluation_grade(별점), evaluated_num(평가 인원 수) 추가
ALTER TABLE repair_man ADD COLUMN evaluation_grade DOUBLE DEFAULT 0.0;
ALTER TABLE repair_man ADD COLUMN evaluated_num INT DEFAULT 0;

-- changeset 0tae:reserve-2 labels:v1,1.2,repair_man_service
-- comment: feat : status(수리 상태), isReviewed(평가 여부) 추가
ALTER TABLE reserve ADD COLUMN status INT DEFAULT 0;
ALTER TABLE reserve ADD COLUMN is_reviewed BOOL DEFAULT false;


-- changeset 0tae:previous_repair-1 labels:v2,1.2,repair_man_service
-- comment: feat : 이전 수리 내역 previous_repair 테이블 생성
CREATE TABLE previous_repair (
                         id INT PRIMARY KEY AUTO_INCREMENT,
                         start_time TIMESTAMP NOT NULL,
                         end_time TIMESTAMP NOT NULL,
                         user_id INT NOT NULL,
                         car_id INT NOT NULL,
                         repair_man_id INT NOT NULL,
                         repair_shop_id INT NOT NULL,
                         maintenance_item_id INT NOT NULL,
                         created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         deleted_at TIMESTAMP,
                         status INT DEFAULT 0,

                         FOREIGN KEY (car_id) REFERENCES car(id),
                         FOREIGN KEY (repair_man_id) REFERENCES repair_man(id),
                         FOREIGN KEY (repair_shop_id) REFERENCES repair_shop(id),
                         FOREIGN KEY (maintenance_item_id) REFERENCES maintenance_item(id),
                         FOREIGN KEY (user_id) REFERENCES user(id)
);

-- changeset 0tae:previous_repair-2 labels:v2,1.2,repair_man_service
-- comment: fix : 정비 날짜 컬럼 추가.
ALTER TABLE previous_repair ADD COLUMN repair_date DATE NOT NULL;
