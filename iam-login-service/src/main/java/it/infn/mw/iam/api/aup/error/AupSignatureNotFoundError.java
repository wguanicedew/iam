package it.infn.mw.iam.api.aup.error;

import it.infn.mw.iam.persistence.model.IamAccount;

public class AupSignatureNotFoundError extends RuntimeException {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public AupSignatureNotFoundError(IamAccount account) {
    super(String.format("AUP signature not found for user '%s'", account.getUsername()));
  }

  

}
