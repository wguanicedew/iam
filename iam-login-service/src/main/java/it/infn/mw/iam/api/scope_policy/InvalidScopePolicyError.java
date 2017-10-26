package it.infn.mw.iam.api.scope_policy;

public class InvalidScopePolicyError extends RuntimeException {

  /**
   * 
   */
  private static final long serialVersionUID = -6852696255567722424L;

  public InvalidScopePolicyError(String message) {
    super(message);
  }

}
