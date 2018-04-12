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

-- Add attribute_id column
ALTER TABLE iam_saml_id ADD COLUMN (attribute_id varchar(256));
-- Create index
CREATE INDEX IDX_IAM_SAML_ID_1 ON iam_saml_id(IDPID, attribute_id, USERID);
-- Update existing records, so that epuid is the default attribute id
update iam_saml_id set attribute_id = 'urn:oid:1.3.6.1.4.1.5923.1.1.1.13' where attribute_id is NULL;
-- Add not null constraint on attribute id
ALTER TABLE iam_saml_id MODIFY attribute_id varchar(256) NOT NULL; 