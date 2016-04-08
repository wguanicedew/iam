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
  (1, 'http://client/');

INSERT INTO client_grant_type (owner_id, grant_type) VALUES
  (1, 'authorization_code'),
  (1, 'urn:ietf:params:oauth:grant_type:redelegate'),
  (1, 'implicit'),
  (1, 'refresh_token');

INSERT INTO iam_account_user_info(ID,NAME, FAMILYNAME, EMAIL, EMAILVERIFIED) VALUES
(1,'Administrator', 'User', 'test@test.org', true),
(2,'Andrea', 'Ceccanti', 'andrea.ceccanti@cnaf.infn.it', true),
(3,'Test', 'User', 'test_user@example.org', true),
(4,'Marco', 'Caberletti', 'marco.caberletti@gmail.com', true);

INSERT INTO saml_account(ID, IDPID, USERID) VALUES
(1,'http://idp.ssocircle.com','andrea.ceccanti@gmail.com'),
(2, 'https://idptestbed/idp/shibboleth','rR4/ziR3RpyGL0fvqzzEy3pxdb4=');

INSERT INTO oidc_account(ID, ISSUER, SUBJECT) VALUES
(1,'http://mitre-srv:8080/openid-connect-server-webapp/','12345.ABCDEFGH'),
(2,'https://accounts.google.com','105440632287425289613');

INSERT INTO iam_account(id,uuid,username,password, iam_user_info_id, SAMLACCOUNT_ID, OIDC_ACCOUNT_ID) VALUES
 (1, '6a987f19-e632-402c-a170-e30f29023823','admin','password',1,1,null),
 (2, 'b3ec49b4-9a74-436c-b4b7-e63df9585c2b', 'andrea', 'password',2,2,null),
 (3, '7d5c7590-c88f-485c-96e3-ccd19fd4bb04', 'test_user', 'password',3,null,1),
 (4, '10fc0984-ba10-4886-91fb-fda805a15b5c', 'marco', 'password',4,null,2);

INSERT INTO authority(ID, AUTH) VALUES
(1, 'ROLE_ADMIN'),
(2, 'ROLE_USER');

INSERT INTO iam_account_authority(IamAccount_ID, authorities_ID) VALUES
(1,1),
(1,2),
(2,2),
(3,2),
(4,2);

INSERT INTO iam_group(ID, NAME, UUID) VALUES
(1, 'Production', '28680262-7ef8-4565-aa1d-d76b08f74ce1'),
(2, 'Analysis', '0542808a-ebbd-4948-a181-52f9bbdae664'),
(3, 'SoftwareManagers', '27efbd07-6729-46d2-a930-b80db294a129');

INSERT INTO iam_account_group(IAM_ACCOUNT_ID, IAM_GROUP_ID) VALUES
(2,1),
(2,2);
