ALTER TABLE iam_scope_policy ADD matching_policy VARCHAR(6) DEFAULT 'EQ';
UPDATE iam_scope_policy set matching_policy = 'EQ';
ALTER TABLE iam_scope_policy ALTER COLUMN matching_policy SET NOT NULL;
--
INSERT INTO system_scope(scope, description, icon, restricted, default_scope, structured, structured_param_description) 
VALUES 
('urn:indigo-iam.github.io:jwt-profile#wlcg-1.0','Tags a client to use the WLCG JWT profile', null, false, false, true, null),
('urn:indigo-iam.github.io:jwt-profile#iam','Tags a client to use the IAM JWT profile', null, false, false, true, null);