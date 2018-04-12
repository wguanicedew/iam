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

CREATE TABLE iam_scope_policy (ID BIGINT AUTO_INCREMENT NOT NULL, 
  creation_time DATETIME NOT NULL, 
  description VARCHAR(512), 
  last_update_time DATETIME NOT NULL, 
  rule VARCHAR(6) NOT NULL, 
  account_id BIGINT, 
  group_id BIGINT, 
  PRIMARY KEY (ID));
  
CREATE TABLE iam_scope_policy_scope (policy_id BIGINT, scope VARCHAR(256));

CREATE UNIQUE INDEX INDEX_iam_scope_policy_scope_policy_id_scope 
  ON iam_scope_policy_scope (policy_id, scope);

CREATE INDEX INDEX_iam_scope_policy_scope_scope ON iam_scope_policy_scope (scope);

ALTER TABLE iam_scope_policy ADD CONSTRAINT FK_iam_scope_policy_group_id 
  FOREIGN KEY (group_id) REFERENCES iam_group (ID);

ALTER TABLE iam_scope_policy ADD CONSTRAINT FK_iam_scope_policy_account_id 
  FOREIGN KEY (account_id) REFERENCES iam_account (ID);

ALTER TABLE iam_scope_policy_scope ADD CONSTRAINT FK_iam_scope_policy_scope_policy_id 
  FOREIGN KEY (policy_id) REFERENCES iam_scope_policy (ID);

-- insert default policy (allow all scopes)
INSERT INTO iam_scope_policy(id, creation_time, description, last_update_time, rule) 
  VALUES(1, CURRENT_TIMESTAMP(), 'Default Permit ALL policy', CURRENT_TIMESTAMP(), 'PERMIT');