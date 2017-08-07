package it.infn.mw.iam.api.scim.updater;

import java.util.List;

import it.infn.mw.iam.api.scim.model.ScimPatchOperation;

/**
 * Builds a list of {@link AccountUpdater} objects linked to a patch operation
 */
@FunctionalInterface
public interface AccountUpdaterFactory<E, S> {


  /**
   * 
   * @param entity @param u @return
   */
  List<AccountUpdater> getUpdatersForPatchOperation(E entity, ScimPatchOperation<S> u);
}
