package it.infn.mw.iam.core.user.exception;

public class CredentialAlreadyBoundException extends IamAccountException {

  /**
   * 
   */
  private static final long serialVersionUID = -5213327060856570097L;
  
  public CredentialAlreadyBoundException(String message) {
    super(message);
  }

}
