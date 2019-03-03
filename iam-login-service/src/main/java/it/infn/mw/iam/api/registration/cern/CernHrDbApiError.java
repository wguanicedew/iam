package it.infn.mw.iam.api.registration.cern;

public class CernHrDbApiError extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public CernHrDbApiError(String message) {
    super(message);
  }

  public CernHrDbApiError(String message, Throwable cause) {
    super(message, cause);
  }
}
