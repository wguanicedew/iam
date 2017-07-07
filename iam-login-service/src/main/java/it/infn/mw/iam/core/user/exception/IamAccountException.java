package it.infn.mw.iam.core.user.exception;

public class IamAccountException extends RuntimeException {

  /**
   * 
   */
  private static final long serialVersionUID = 2769590935871518008L;

  public IamAccountException() {
    super();
  }

  public IamAccountException(String message, Throwable cause) {
    super(message, cause);
  }

  public IamAccountException(String message) {
    super(message);
  }

  public IamAccountException(Throwable cause) {
    super(cause);
  }
  
}
