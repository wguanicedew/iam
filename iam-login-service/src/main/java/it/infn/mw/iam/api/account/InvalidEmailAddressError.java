package it.infn.mw.iam.api.account;

public class InvalidEmailAddressError extends RuntimeException {


  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public InvalidEmailAddressError(String message) {
    super(message);
  }

}
