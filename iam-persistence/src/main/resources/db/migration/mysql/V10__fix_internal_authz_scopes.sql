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

INSERT INTO system_scope(scope, description, icon, restricted, default_scope, structured, structured_param_description) 
  VALUES
  ('scim','Authorizes access to IAM SCIM APIs', null, true, false, false, null),
  ('registration','Authorizes access to IAM registration APIs', null, true, false, false, null);
  
UPDATE system_scope 
  set structured = true, 
  structured_param_description = 'write access to IAM SCIM APIs'
  where scope = 'scim:write';

UPDATE system_scope 
  set structured = true,
  structured_param_description = 'read access to IAM SCIM APIs'
  where scope = 'scim:read';
  
UPDATE system_scope 
  set structured = true, 
  structured_param_description = 'write access to IAM registration APIs'
  where scope = 'registration:write';

UPDATE system_scope 
  set structured = true,
  structured_param_description = 'read access to IAM registration APIs'
  where scope = 'registration:read';