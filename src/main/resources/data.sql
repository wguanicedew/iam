INSERT INTO system_scope(scope, description, icon, restricted, default_scope, structured, structured_param_description) VALUES
  ('openid', 'log in using your identity', 'user', false, true, false, null),
  ('profile', 'basic profile information', 'list-alt', false, true, false, null),
  ('email', 'email address', 'envelope', false, true, false, null),
  ('address', 'physical address', 'home', false, true, false, null),
  ('phone', 'telephone number', 'bell', false, true, false, null),
  ('offline_access', 'offline access', 'time', false, false, false, null);
  
INSERT INTO client_details (id, client_id, client_secret, client_name, dynamically_registered, refresh_token_validity_seconds, access_token_validity_seconds, id_token_validity_seconds, allow_introspection) VALUES
  (1, 'client', 'secret', 'Test Client', false, null, 3600, 600, true);

INSERT INTO client_scope (owner_id, scope) VALUES
  (1, 'openid'),
  (1, 'profile'),
  (1, 'email'),
  (1, 'address'),
  (1, 'phone'),
  (1, 'offline_access');

INSERT INTO client_redirect_uri (owner_id, redirect_uri) VALUES
  (1, 'http://localhost/'),
  (1, 'http://localhost:9999/');
  
INSERT INTO client_grant_type (owner_id, grant_type) VALUES
  (1, 'authorization_code'),
  (1, 'urn:ietf:params:oauth:grant_type:redelegate'),
  (1, 'implicit'),
  (1, 'refresh_token');

INSERT INTO iam_account_user_info(ID,NAME, FAMILYNAME, EMAIL, EMAILVERIFIED) VALUES
(1,'Administrator', 'User', 'test@test.org', true);

INSERT INTO saml_account(ID, IDPID, USERID) VALUES
(1,'http://idp.ssocircle.com','andrea.ceccanti@gmail.com');

INSERT INTO iam_account(id,uuid,username,password, iam_user_info_id, SAMLACCOUNT_ID) VALUES
 (1, '6a987f19-e632-402c-a170-e30f29023823','admin','password',1,1);

INSERT INTO authority(ID, AUTH) VALUES
(1, 'ROLE_ADMIN'),
(2, 'ROLE_USER');

INSERT INTO iam_account_authority(IamAccount_ID, authorities_ID) VALUES
(1,1),
(1,2);