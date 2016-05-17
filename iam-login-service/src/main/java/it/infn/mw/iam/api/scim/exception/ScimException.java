package it.infn.mw.iam.api.scim.exception;

public class ScimException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public ScimException() {
    super();

  }

  public ScimException(String message, Throwable cause) {
    super(message, cause);

  }

  public ScimException(String message) {
    super(message);
  }

  public ScimException(Throwable cause) {
    super(cause);
  }

}
