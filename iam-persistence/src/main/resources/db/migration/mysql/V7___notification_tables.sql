CREATE TABLE iam_email_notification (ID BIGINT AUTO_INCREMENT NOT NULL, UUID VARCHAR(36) NOT NULL UNIQUE, NOTIFICATION_TYPE VARCHAR(128) NOT NULL, SUBJECT VARCHAR(128), BODY TEXT, CREATION_TIME TIMESTAMP NOT NULL, DELIVERY_STATUS VARCHAR(128), LAST_UPDATE TIMESTAMP, PRIMARY KEY (ID));
CREATE TABLE iam_notification_receiver(ID BIGINT AUTO_INCREMENT NOT NULL, NOTIFICATION_ID BIGINT, ACCOUNT_ID BIGINT, PRIMARY KEY (ID));
ALTER TABLE iam_notification_receiver ADD CONSTRAINT FK_iam_notification_receiver_notification_id FOREIGN KEY (notification_id) REFERENCES iam_email_notification (id);
ALTER TABLE iam_notification_receiver ADD CONSTRAINT FK_iam_notification_receiver_account_id FOREIGN KEY (account_id) REFERENCES iam_account (id);
ALTER TABLE iam_reg_request ADD COLUMN notification_id BIGINT;
ALTER TABLE iam_reg_request ADD CONSTRAINT FK_iam_reg_request_notification_id FOREIGN KEY (notification_id) REFERENCES iam_email_notification(id);
