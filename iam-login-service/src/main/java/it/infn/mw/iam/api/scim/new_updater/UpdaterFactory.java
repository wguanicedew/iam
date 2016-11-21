package it.infn.mw.iam.api.scim.new_updater;

import java.util.List;

import it.infn.mw.iam.api.scim.model.ScimPatchOperation;

/**
 * Builds a list of {@link Updater} objects linked to a patch operation
 */
public interface UpdaterFactory<EntityType, ScimType> {


  /**
   * 
   * @param entity @param u @return
   */
  List<Updater> getUpdatersForPatchOperation(EntityType entity, ScimPatchOperation<ScimType> u);
}
