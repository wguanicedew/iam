package it.infn.mw.iam.api.scim.new_updater;

public enum AccountUpdater {

  REPLACE_GIVEN_NAME,
  REPLACE_FAMILY_NAME,
  REPLACE_EMAIL,
  REPLACE_PASSWORD,
  REPLACE_PICTURE,

  ADD_OIDC_ID,
  ADD_SAML_ID,
  ADD_SSH_KEY,

  REMOVE_SAML_ID,
  REMOVE_OIDC_ID;

}
