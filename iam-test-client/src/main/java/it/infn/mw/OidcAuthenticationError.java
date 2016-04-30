package it.infn.mw;

import org.springframework.security.core.AuthenticationException;

public class OidcAuthenticationError extends AuthenticationException {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private final String error;
  private final String errorDescription;
  private final String errorUri;

  public OidcAuthenticationError(String msg, String error,
    String errorDescription, String errorUri) {
    super(msg);
    this.error = error;
    this.errorDescription = errorDescription;
    this.errorUri = errorUri;
  }

  public String getError() {

    return error;
  }

  public String getErrorDescription() {

    return errorDescription;
  }

  public String getErrorUri() {

    return errorUri;
  }

}
