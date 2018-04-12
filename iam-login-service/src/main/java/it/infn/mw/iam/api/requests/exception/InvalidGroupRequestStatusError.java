package it.infn.mw.iam.api.requests.exception;

public class InvalidGroupRequestStatusError extends IllegalArgumentException {

  private static final long serialVersionUID = 1L;

  public InvalidGroupRequestStatusError(String message) {
    super(message);
  }
}
