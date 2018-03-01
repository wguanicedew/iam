package it.infn.mw.iam.persistence.repository;

import it.infn.mw.iam.persistence.model.IamAccount;

public class IamAupSignatureNotFoundError extends RuntimeException {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public IamAupSignatureNotFoundError(IamAccount account) {
    super(String.format("AUP signature not found for user '%s'", account.getUsername()));
  }
}
