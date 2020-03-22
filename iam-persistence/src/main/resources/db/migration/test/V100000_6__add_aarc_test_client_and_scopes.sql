INSERT INTO system_scope(scope, description, icon, restricted, default_scope, structured, structured_param_description) VALUES
  ('eduperson_scoped_affiliation', 'aarc organisation name', 'null', false, false, false, null),
  ('eduperson_entitlement', 'aarc groups', 'null', false, false, false, null);

INSERT INTO client_scope (owner_id, scope) VALUES
  (5, 'eduperson_scoped_affiliation'),
  (5, 'eduperson_entitlement');