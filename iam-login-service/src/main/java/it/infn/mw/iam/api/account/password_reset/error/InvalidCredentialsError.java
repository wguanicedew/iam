package it.infn.mw.iam.api.account.password_reset.error;

public class InvalidCredentialsError extends RuntimeException {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public InvalidCredentialsError(String message) {
    super(message);
  }

}
