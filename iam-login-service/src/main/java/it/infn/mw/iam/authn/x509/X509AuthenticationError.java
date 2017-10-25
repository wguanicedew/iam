package it.infn.mw.iam.authn.x509;

public class X509AuthenticationError extends RuntimeException {
  
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public X509AuthenticationError(String message) {
    super(message);
  }

  public X509AuthenticationError(Throwable cause) {
    super(cause);
  }

  public X509AuthenticationError(String message, Throwable cause) {
    super(message, cause);
  }

}
