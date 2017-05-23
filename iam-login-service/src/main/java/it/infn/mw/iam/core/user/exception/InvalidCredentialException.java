package it.infn.mw.iam.core.user.exception;

public class InvalidCredentialException extends IamAccountException {

  private static final long serialVersionUID = 7461872494570748516L;

  public InvalidCredentialException(String message) {
    super(message);
  }

}
