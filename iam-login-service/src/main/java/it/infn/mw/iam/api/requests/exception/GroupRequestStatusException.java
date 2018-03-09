package it.infn.mw.iam.api.requests.exception;

public class GroupRequestStatusException extends IllegalArgumentException {

  private static final long serialVersionUID = 1L;

  public GroupRequestStatusException(String message) {
    super(message);
  }
}
