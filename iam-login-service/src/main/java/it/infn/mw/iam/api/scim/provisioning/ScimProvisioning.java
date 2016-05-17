package it.infn.mw.iam.api.scim.provisioning;

public interface ScimProvisioning<T> {

  T getById(String id);

  void delete(String id);

}
