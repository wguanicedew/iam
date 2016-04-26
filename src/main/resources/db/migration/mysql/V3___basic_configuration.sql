INSERT INTO system_scope(scope, description, icon, restricted, default_scope, structured, structured_param_description) VALUES
  ('openid', 'log in using your identity', 'user', false, true, false, null),
  ('profile', 'basic profile information', 'list-alt', false, true, false, null),
  ('email', 'email address', 'envelope', false, true, false, null),
  ('address', 'physical address', 'home', false, true, false, null),
  ('phone', 'telephone number', 'bell', false, true, false, null),
  ('offline_access', 'offline access', 'time', false, false, false, null);

INSERT INTO iam_authority(ID, AUTH) VALUES
(1, 'ROLE_ADMIN'),
(2, 'ROLE_USER');

INSERT INTO iam_account_user_info(ID,NAME, FAMILYNAME, EMAIL, EMAILVERIFIED) VALUES
(1,'Administrator', 'User', 'admin@test.org', true);

INSERT INTO iam_account(id,uuid,username,password, iam_user_info_id, SAMLACCOUNT_ID, OIDC_ACCOUNT_ID) VALUES
(1, '6a987f19-e632-402c-a170-e30f29023823','admin','password',1,null,null);

INSERT INTO iam_account_authority(iam_account_id, iam_authority_id) VALUES
(1,1),
(1,2);