package it.infn.mw.iam.api.account_linking;

public class AccountLinkingDisabledException extends RuntimeException {
  
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public static final String MESSAGE = "Account linking is disabled for this IAM instance";
  
  public AccountLinkingDisabledException() {
    super(MESSAGE);
  }

}
