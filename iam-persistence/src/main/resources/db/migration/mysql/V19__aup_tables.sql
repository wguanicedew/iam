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

CREATE TABLE iam_aup (ID BIGINT AUTO_INCREMENT NOT NULL, 
  creation_time DATETIME NOT NULL, description VARCHAR(128), last_update_time DATETIME NOT NULL, 
  name VARCHAR(36) NOT NULL UNIQUE, 
  sig_validity_days BIGINT NOT NULL, 
  text LONGTEXT NOT NULL, 
  PRIMARY KEY (ID));
  
CREATE TABLE iam_aup_signature (ID BIGINT AUTO_INCREMENT NOT NULL, 
  signature_time DATETIME NOT NULL, 
  account_id BIGINT, 
  aup_id BIGINT, 
  PRIMARY KEY (ID));

ALTER TABLE iam_aup_signature ADD CONSTRAINT 
  UNQ_iam_aup_signature_0 UNIQUE (aup_id, account_id);

ALTER TABLE iam_aup_signature ADD CONSTRAINT FK_iam_aup_signature_aup_id 
  FOREIGN KEY (aup_id) REFERENCES iam_aup (ID);

ALTER TABLE iam_aup_signature ADD CONSTRAINT FK_iam_aup_signature_account_id 
  FOREIGN KEY (account_id) REFERENCES iam_account (ID);