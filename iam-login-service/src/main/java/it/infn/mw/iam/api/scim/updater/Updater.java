package it.infn.mw.iam.api.scim.updater;

import java.util.List;

import it.infn.mw.iam.api.scim.model.ScimPatchOperation;

public interface Updater<S> {

  void update(S entity, List<? extends ScimPatchOperation> operations);

}

