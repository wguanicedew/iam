package it.infn.mw.iam.api.scim.provisioning;

import java.util.List;

import it.infn.mw.iam.api.scim.model.ScimListResponse;
import it.infn.mw.iam.api.scim.model.ScimPatchOperation;
import it.infn.mw.iam.api.scim.provisioning.paging.ScimPageRequest;

public interface ScimProvisioning<T, E> {

  T getById(String id);

  T create(T newScimItem);

  T replace(String id, T scimItemToBeReplaced);

  void update(String id, List<ScimPatchOperation<E>> operations);

  void delete(String id);

  ScimListResponse<T> list(ScimPageRequest params);

}
