CREATE TABLE SEQUENCE (SEQ_NAME VARCHAR(50) NOT NULL, SEQ_COUNT NUMERIC(38), PRIMARY KEY (SEQ_NAME));

INSERT INTO SEQUENCE(SEQ_NAME, SEQ_COUNT) values ('SEQ_GEN', 0);

INSERT INTO system_scope(scope, description, icon, restricted, default_scope, structured, structured_param_description) VALUES
  ('openid', 'log in using your identity', 'user', false, true, false, null),
  ('profile', 'basic profile information', 'list-alt', false, true, false, null),
  ('email', 'email address', 'envelope', false, true, false, null),
  ('address', 'physical address', 'home', false, true, false, null),
  ('phone', 'telephone number', 'bell', false, true, false, null),
  ('offline_access', 'offline access', 'time', false, false, false, null),
  ('scim:read','read access to SCIM user and groups', null, true, false, false, null),
  ('scim:write','write access to SCIM user and groups', null, true, false, false, null);

INSERT INTO client_details (id, client_id, client_secret, client_name, dynamically_registered, refresh_token_validity_seconds, access_token_validity_seconds, id_token_validity_seconds, allow_introspection, token_endpoint_auth_method) VALUES
  (1, 'client', 'secret', 'Test Client', false, null, 3600, 600, true, 'SECRET_BASIC'),
  (2, 'tasks-app', 'secret', 'Tasks App', false, null, 0, 0, true, 'SECRET_BASIC'),
  (3, 'post-client', 'secret', 'Post client', false, null, 3600,600, true, 'SECRET_POST'),
  (4, 'client-cred', 'secret', 'Client credentials', false, null, 3600, 600, true, 'SECRET_BASIC'),
  (5, 'password-grant', 'secret', 'Password grant client', false, null, 3600, 600, true, 'SECRET_BASIC'),
  (6, 'scim-client-ro', 'secret', 'SCIM client (read-only)', false, null, 3600, 600, true, 'SECRET_POST'),
  (7, 'scim-client-rw', 'secret', 'SCIM client (read-write)', false, null, 3600, 600, true, 'SECRET_POST');

INSERT INTO client_scope (owner_id, scope) VALUES
  (1, 'openid'),
  (1, 'profile'),
  (1, 'email'),
  (1, 'address'),
  (1, 'phone'),
  (1, 'offline_access'),
  (1, 'read-tasks'),
  (1, 'write-tasks'),
  (2, 'openid'),
  (2, 'profile'),
  (2, 'read-tasks'),
  (2, 'write-tasks'),
  (3, 'openid'),
  (3, 'profile'),
  (3, 'read-tasks'),
  (3, 'write-tasks'),
  (4, 'openid'),
  (4, 'profile'),
  (4, 'read-tasks'),
  (4, 'write-tasks'),
  (5, 'openid'),
  (5, 'profile'),
  (5, 'email'),
  (5, 'address'),
  (5, 'phone'),
  (5, 'offline_access'),
  (6, 'openid'),
  (6, 'profile'),
  (6, 'email'),
  (6, 'address'),
  (6, 'phone'),
  (6, 'offline_access'),
  (6, 'scim:read'),
  (7, 'openid'),
  (7, 'profile'),
  (7, 'email'),
  (7, 'address'),
  (7, 'phone'),
  (7, 'offline_access'),
  (7, 'scim:read'),
  (7, 'scim:write');
  
  
INSERT INTO client_redirect_uri (owner_id, redirect_uri) VALUES
  (1, 'http://localhost:9090/iam-test-client/openid_connect_login'),
  (1, 'https://iam.local.io/iam-test-client/openid_connect_login'),
  (3, 'http://localhost:4000/callback'),
  (4, 'http://localhost:5000/callback');

  INSERT INTO client_grant_type (owner_id, grant_type) VALUES
  (1, 'authorization_code'),
  (1, 'urn:ietf:params:oauth:grant_type:redelegate'),
  (1, 'implicit'),
  (1, 'refresh_token'),
  (3, 'authorization_code'),
  (3, 'client_credentials'),
  (4, 'password'),
  (4, 'client_credentials'),
  (5, 'password'),
  (6, 'client_credentials'),
  (7, 'client_credentials');
  
INSERT INTO iam_authority(ID, AUTH) VALUES
(1, 'ROLE_ADMIN'),
(2, 'ROLE_USER');
  
INSERT INTO iam_user_info(ID,GIVENNAME,FAMILYNAME, EMAIL, EMAILVERIFIED) VALUES
(1, 'Test', 'User', 'test@iam.test', true),
(2, 'Admin', 'User', 'admin@iam.test', true);

INSERT INTO iam_account(id, uuid, username, password, user_info_id, creationtime, lastupdatetime, active) VALUES
(1, '80e5fb8d-b7c8-451a-89ba-346ae278a66f', 'test', 'password', 1, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), true),
(2, '73f16d93-2441-4a50-88ff-85360d78c6b5', 'admin', 'password', 2, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), true);

INSERT INTO iam_oidc_id(issuer, subject, account_id) VALUES
('https://accounts.google.com', '105440632287425289613', 1),
('https://accounts.google.com', '112972579367980197252', 2);

INSERT INTO iam_group(id, name, uuid, description) VALUES
(1, 'Production', 'c617d586-54e6-411d-8e38-64967798fa8a', 'The INDIGO-DC production group'),
(2, 'Analysis', '6a384bcd-d4b3-4b7f-a2fe-7d897ada0dd1', 'The INDIGO-DB analysis group');

INSERT INTO iam_account_group(account_id, group_id) VALUES
(1,1),
(1,2),
(2,1),
(2,2);

INSERT INTO iam_account_authority(account_id, authority_id) VALUES
(1,1),
(1,2),
(2,1),
(2,2);