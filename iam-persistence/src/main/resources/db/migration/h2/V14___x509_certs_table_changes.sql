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

-- Drop not-null constraint on certificate 
ALTER TABLE iam_x509_cert ALTER COLUMN CERTIFICATE SET NULL;
-- Add unique index on certificate
ALTER TABLE iam_x509_cert ADD CONSTRAINT uniq_iam_x509_cert_cerificate UNIQUE(certificate) CHECK;
-- Rename certificatesubject to subject_dn
ALTER TABLE iam_x509_cert ALTER COLUMN CERTIFICATESUBJECT RENAME TO subject_dn;
-- Add issuer_dn column
ALTER TABLE iam_x509_cert ADD COLUMN issuer_dn VARCHAR(128) NOT NULL;
-- Add creation_time column
ALTER TABLE iam_x509_cert ADD COLUMN creation_time TIMESTAMP NOT NULL;
-- Add last_update_time column
ALTER TABLE iam_x509_cert ADD COLUMN last_update_time TIMESTAMP NOT NULL;