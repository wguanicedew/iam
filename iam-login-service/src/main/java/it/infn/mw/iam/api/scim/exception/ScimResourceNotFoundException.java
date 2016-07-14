package it.infn.mw.iam.api.scim.exception;

public class ScimResourceNotFoundException extends ScimException {

  private static final long serialVersionUID = 1L;

  public ScimResourceNotFoundException(String message) {
    super(message);
  }

}
