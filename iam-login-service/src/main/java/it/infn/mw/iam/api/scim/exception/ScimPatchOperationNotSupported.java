package it.infn.mw.iam.api.scim.exception;

public class ScimPatchOperationNotSupported extends ScimException {

  private static final long serialVersionUID = 1L;

  public ScimPatchOperationNotSupported(String message) {
	super(message);
  }

}
