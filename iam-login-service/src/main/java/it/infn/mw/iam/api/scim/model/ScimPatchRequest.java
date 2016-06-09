package it.infn.mw.iam.api.scim.model;

import java.util.List;
import java.util.Set;

public interface ScimPatchRequest {
  
  public Set<String> getSchemas();

  public List<? extends ScimPatchOperation> getOperations();

}
