-- Add creation time to ssh keys
ALTER TABLE iam_ssh_key ADD COLUMN creation_time DATETIME NOT null;
-- Add last update time to ssh keys
ALTER TABLE iam_ssh_key ADD COLUMN last_update_time DATETIME NOT null;