package it.infn.mw.iam.api.requests.exception;

public class UserMismatchException extends IllegalArgumentException {

  private static final long serialVersionUID = 1L;

  public UserMismatchException(String message) {
    super(message);
  }
}
