package it.infn.mw.iam.api.scim.provisioning;

public interface ScimProvisioning<T> {

  T getById(String id);

  T create(T newScimItem);

  void delete(String id);

}
