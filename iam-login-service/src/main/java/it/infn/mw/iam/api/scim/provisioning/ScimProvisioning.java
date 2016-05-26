package it.infn.mw.iam.api.scim.provisioning;

import it.infn.mw.iam.api.scim.model.ScimListResponse;
import it.infn.mw.iam.api.scim.provisioning.paging.ScimPageRequest;

public interface ScimProvisioning<T> {

  T getById(String id);

  T create(T newScimItem);
  
  T replace(String id, T scimItemToBeUpdated);
  
  void delete(String id);
  
  ScimListResponse<T> list(ScimPageRequest params);  

}
