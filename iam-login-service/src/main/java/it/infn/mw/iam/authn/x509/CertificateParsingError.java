package it.infn.mw.iam.authn.x509;

public class CertificateParsingError extends RuntimeException {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public CertificateParsingError(String message) {
    super(message);
  }

  public CertificateParsingError(String message, Throwable cause) {
    super(message, cause);
  }
}
