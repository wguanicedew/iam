package it.infn.mw.iam.api.requests.exception;

public class GroupRequestValidationException extends IllegalArgumentException {

  private static final long serialVersionUID = 1L;

  public GroupRequestValidationException(String message) {
    super(message);
  }
}
