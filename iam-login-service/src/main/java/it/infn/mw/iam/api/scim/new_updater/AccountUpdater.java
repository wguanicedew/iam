package it.infn.mw.iam.api.scim.new_updater;

public enum AccountUpdater {

  addGivenName,
  addFamilyName,
  addEmail,
  addPassword,
  addPicture,

  addOidcId,
  addSamlId,

  removeSamlId,
  removeOidcId;

}
