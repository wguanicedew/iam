package it.infn.mw.iam.api.scim.model;

public enum ScimSchemas {

  CORE_USER_SCHEMA("urn:ietf:params:scim:schemas:core:2.0:User");

  private final String value;

  private ScimSchemas(String v) {
    value = v;
  }

  public String value() {

    return value;
  }
}
