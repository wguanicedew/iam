CREATE TABLE iam_scope (ID BIGINT AUTO_INCREMENT NOT NULL, SCOPE VARCHAR(256) NOT NULL UNIQUE, PRIMARY KEY (ID));
CREATE TABLE iam_scope_policy (ID BIGINT AUTO_INCREMENT NOT NULL, creation_time DATETIME NOT NULL, 
  description VARCHAR(512), last_update_time DATETIME NOT NULL, rule VARCHAR(6) NOT NULL, 
  account_id BIGINT, group_id BIGINT, PRIMARY KEY (ID));
CREATE TABLE iam_scope_policy_scope (policy_id BIGINT NOT NULL, scope_id BIGINT NOT NULL, 
  PRIMARY KEY (policy_id, scope_id));
  
ALTER TABLE iam_scope_policy 
  ADD CONSTRAINT FK_iam_scope_policy_group_id FOREIGN KEY (group_id) REFERENCES iam_group (ID);

ALTER TABLE iam_scope_policy 
  ADD CONSTRAINT FK_iam_scope_policy_account_id FOREIGN KEY (account_id) REFERENCES iam_account (ID);

ALTER TABLE iam_scope_policy_scope 
  ADD CONSTRAINT FK_iam_scope_policy_scope_policy_id FOREIGN KEY (policy_id) REFERENCES iam_scope_policy (ID);

ALTER TABLE iam_scope_policy_scope 
  ADD CONSTRAINT FK_iam_scope_policy_scope_scope_id FOREIGN KEY (scope_id) REFERENCES iam_scope (ID);

-- insert default policy (allow all scopes)
INSERT INTO iam_scope_policy(id, creation_time, description, last_update_time, rule) 
  VALUES(1, CURRENT_TIMESTAMP(), 'Default Permit ALL policy', CURRENT_TIMESTAMP(), 'PERMIT');