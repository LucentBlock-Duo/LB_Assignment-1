-- liquibase formatted sql

-- changeset 0tae:changelog-reserve.1.1
ALTER TABLE reserve MODIFY id INT NOT NULL AUTO_INCREMENT;

-- changeset 0tae:changelog-login_challenge.1.1
ALTER TABLE login_challenge MODIFY id INT NOT NULL AUTO_INCREMENT;

-- changeset 0tae:changelog-signup_code_challenge.1.1
ALTER TABLE signup_code_challenge MODIFY id INT NOT NULL AUTO_INCREMENT;