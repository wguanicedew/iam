package it.infn.mw.iam.api.scope_policy;

public class ScopePolicyNotFoundError extends RuntimeException {


  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public ScopePolicyNotFoundError(String message) {
    super(message);
  }

}
