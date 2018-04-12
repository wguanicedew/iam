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

CREATE TABLE iam_group_request (
  ID BIGINT IDENTITY NOT NULL,
  UUID VARCHAR(36) NOT NULL UNIQUE,
  ACCOUNT_ID BIGINT,
  GROUP_ID BIGINT,
  STATUS VARCHAR(50),
  NOTES CLOB,
  MOTIVATION CLOB,
  CREATIONTIME TIMESTAMP NOT NULL,
  LASTUPDATETIME TIMESTAMP,
  PRIMARY KEY (ID));

ALTER TABLE iam_group_request ADD CONSTRAINT FK_iam_group_request_account_id FOREIGN KEY (ACCOUNT_ID) REFERENCES iam_account (ID);
ALTER TABLE iam_group_request ADD CONSTRAINT FK_iam_group_request_group_id FOREIGN KEY (GROUP_ID) REFERENCES iam_group (ID);
