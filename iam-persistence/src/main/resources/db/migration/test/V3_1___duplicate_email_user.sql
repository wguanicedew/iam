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

INSERT INTO iam_user_info(ID,GIVENNAME,FAMILYNAME, EMAIL, EMAILVERIFIED, BIRTHDATE, GENDER) VALUES
(3, 'Duplicate', 'Email 0', 'admin@iam.test', true, '1950-01-01', 'M');

INSERT INTO iam_account(id, uuid, username, password, user_info_id, creationtime, lastupdatetime, active) VALUES
(3, 'bffc67b7-47fe-410c-a6a0-cf00173a8fbb', 'dup_email_0', '$2a$10$UZeOZKD1.dj5oiTsZKD03OETA9FXCKGqBuuijhsxYygZpOPtWMUni', 
3, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), true);

INSERT INTO iam_user_info(ID,GIVENNAME,FAMILYNAME, EMAIL, EMAILVERIFIED, BIRTHDATE, GENDER) VALUES
(4, 'Duplicate ', 'Email 1', 'duplicate@iam.test', true, '1950-01-01', 'M');

INSERT INTO iam_account(id, uuid, username, password, user_info_id, creationtime, lastupdatetime, active) VALUES
(4, '0a6fa72a-fb75-4a6c-9734-bfe673df70b3', 'dup_email_1', '$2a$10$UZeOZKD1.dj5oiTsZKD03OETA9FXCKGqBuuijhsxYygZpOPtWMUni', 
4, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), true);

INSERT INTO iam_user_info(ID,GIVENNAME,FAMILYNAME, EMAIL, EMAILVERIFIED, BIRTHDATE, GENDER) VALUES
(5, 'Duplicate ', 'Email 2', 'duplicate@iam.test', true, '1950-01-01', 'M');

INSERT INTO iam_account(id, uuid, username, password, user_info_id, creationtime, lastupdatetime, active) VALUES
(5, 'd836e5ec-246c-456c-8476-923ee2f831c8', 'dup_email_2', '$2a$10$UZeOZKD1.dj5oiTsZKD03OETA9FXCKGqBuuijhsxYygZpOPtWMUni', 
5, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), true);