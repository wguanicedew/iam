package it.infn.mw.iam.api.scim.updater;

public enum UpdaterType {

  ACCOUNT_REPLACE_GIVEN_NAME("Replace user's given name"),
  ACCOUNT_REPLACE_FAMILY_NAME("Replace user's family name"),
  ACCOUNT_REPLACE_EMAIL("Replace user's email"),
  ACCOUNT_REPLACE_PASSWORD("Replace user's password"),
  ACCOUNT_REPLACE_PICTURE("Replace user's picture"),
  ACCOUNT_REPLACE_USERNAME("Replace user's username"),
  ACCOUNT_REPLACE_ACTIVE("Replace user's active status"),

  ACCOUNT_ADD_OIDC_ID("Add OpenID Connect account to user"),
  ACCOUNT_ADD_SAML_ID("Add SAML account to user"),
  ACCOUNT_ADD_SSH_KEY("Add ssh key to user"),
  ACCOUNT_ADD_X509_CERTIFICATE("Add x509 certificate to user"),

  ACCOUNT_REMOVE_OIDC_ID("Remove user's OpenID Connect account"),
  ACCOUNT_REMOVE_SAML_ID("Remove user's SAML account"),
  ACCOUNT_REMOVE_SSH_KEY("Remove user's ssh key"),
  ACCOUNT_REMOVE_X509_CERTIFICATE("Remove user's x509 certificate"),

  ACCOUNT_ADD_GROUP_MEMBERSHIP("Add group membership to user"),
  ACCOUNT_REMOVE_GROUP_MEMBERSHIP("Remove user's group membership");

  private String description;

  UpdaterType(String description) {
    this.description = description;
  }

  public String getDescription() {
    return description;
  }
}