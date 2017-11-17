CREATE TABLE iam_aup (ID BIGINT IDENTITY NOT NULL, 
  creation_time TIMESTAMP NOT NULL, 
  description VARCHAR(128), 
  last_update_time TIMESTAMP NOT NULL, 
  name VARCHAR(36) NOT NULL UNIQUE, 
  sig_validity_days BIGINT NOT NULL, 
  text LONGVARCHAR NOT NULL, 
  PRIMARY KEY (ID));