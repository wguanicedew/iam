package it.infn.mw.iam.api.requests.exception;

public class GroupRequestValidationError extends IllegalArgumentException {

  private static final long serialVersionUID = 1L;

  public GroupRequestValidationError(String message) {
    super(message);
  }
}
