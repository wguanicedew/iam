package it.infn.mw.iam.rcauth.x509;

public class ProxyGenerationError extends RuntimeException {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public ProxyGenerationError(Throwable cause) {
    super(cause);
  }

  public ProxyGenerationError(String message, Throwable cause) {
    super(message, cause);
  }
}
