package it.infn.mw.iam.core.user.exception;

public class UserAlreadyExistsException extends IamAccountException {

  /**
   * 
   */
  private static final long serialVersionUID = 4103663720620113509L;

  public UserAlreadyExistsException(String message) {
    super(message);
  }

}
