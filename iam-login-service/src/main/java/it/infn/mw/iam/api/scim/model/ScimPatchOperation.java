package it.infn.mw.iam.api.scim.model;

public interface ScimPatchOperation {

  public static enum ScimPatchOperationType {
	add, remove, replace
  }

  ScimPatchOperationType getOp();

  Object getValue();

  String getPath();

}
