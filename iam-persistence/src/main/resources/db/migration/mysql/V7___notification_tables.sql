--
-- Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2016-2018
--
-- Licensed under the Apache License, Version 2.0 (the "License");
-- you may not use this file except in compliance with the License.
-- You may obtain a copy of the License at
--
--     http://www.apache.org/licenses/LICENSE-2.0
--
-- Unless required by applicable law or agreed to in writing, software
-- distributed under the License is distributed on an "AS IS" BASIS,
-- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
-- See the License for the specific language governing permissions and
-- limitations under the License.
--

CREATE TABLE iam_email_notification (ID BIGINT AUTO_INCREMENT NOT NULL, UUID VARCHAR(36) NOT NULL UNIQUE, NOTIFICATION_TYPE VARCHAR(128) NOT NULL, SUBJECT VARCHAR(128), BODY TEXT, CREATION_TIME TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, DELIVERY_STATUS VARCHAR(128), LAST_UPDATE TIMESTAMP NULL DEFAULT NULL, REQUEST_ID BIGINT, PRIMARY KEY (ID));
ALTER TABLE iam_email_notification ADD CONSTRAINT FK_iam_email_notification_request_id FOREIGN KEY (request_id) REFERENCES iam_reg_request (id);
CREATE TABLE iam_notification_receiver(ID BIGINT AUTO_INCREMENT NOT NULL, NOTIFICATION_ID BIGINT, EMAIL_ADDRESS VARCHAR(254), PRIMARY KEY (ID));
ALTER TABLE iam_notification_receiver ADD CONSTRAINT FK_iam_notification_receiver_notification_id FOREIGN KEY (notification_id) REFERENCES iam_email_notification (id);
ALTER TABLE iam_reg_request ADD COLUMN notes TEXT;