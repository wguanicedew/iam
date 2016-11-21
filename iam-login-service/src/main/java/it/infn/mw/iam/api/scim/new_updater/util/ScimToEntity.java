package it.infn.mw.iam.api.scim.new_updater.util;

@FunctionalInterface
public interface ScimToEntity<ScimType, EntityType> {

  EntityType fromScim(ScimType s);
}
