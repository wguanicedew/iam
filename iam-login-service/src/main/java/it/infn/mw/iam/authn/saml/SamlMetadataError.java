package it.infn.mw.iam.authn.saml;

public class SamlMetadataError extends RuntimeException {


  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public SamlMetadataError(String message) {
    super(message);
  }

  public SamlMetadataError(Throwable cause) {
    super(cause);
  }

  public SamlMetadataError(String message, Throwable cause) {
    super(message, cause);
  }

}
