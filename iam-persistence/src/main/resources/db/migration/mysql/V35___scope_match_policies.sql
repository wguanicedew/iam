ALTER TABLE iam_scope_policy ADD matching_policy VARCHAR(6);
UPDATE iam_scope_policy set matching_policy = 'EQ';
ALTER TABLE iam_scope_policy MODIFY matching_policy VARCHAR(6) default 'EQ' NOT NULL;