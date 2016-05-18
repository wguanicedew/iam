package it.infn.mw.iam.api.scim.exception;

public class ScimValidationException extends IllegalArgumentException {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public ScimValidationException(String message) {
    super(message);
  }

}
