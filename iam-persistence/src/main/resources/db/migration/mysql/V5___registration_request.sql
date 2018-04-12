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

CREATE TABLE iam_reg_request (ID BIGINT AUTO_INCREMENT NOT NULL, UUID VARCHAR(36) NOT NULL UNIQUE, CREATIONTIME TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, ACCOUNT_ID BIGINT, STATUS VARCHAR(50), LASTUPDATETIME TIMESTAMP NULL DEFAULT NULL, PRIMARY KEY (ID));
ALTER TABLE iam_reg_request ADD CONSTRAINT FK_iam_reg_request_account_id FOREIGN KEY (ACCOUNT_ID) REFERENCES iam_account (ID);
ALTER TABLE iam_account ADD (confirmation_key VARCHAR(36), reset_key VARCHAR(36));
INSERT INTO system_scope(scope, description, icon, restricted, default_scope, structured, structured_param_description) VALUES
  ('registration:read','Grants read access to registration requests', null, true, false, false, null),
  ('registration:write','Grants write access to registration requests', null, true, false, false, null);

