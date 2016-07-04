package it.infn.mw.iam.authn.oidc;

import org.springframework.security.core.AuthenticationException;

import it.infn.mw.iam.authn.AuthenticationExceptionMessageHelper;

public class OidcExceptionMessageHelper implements AuthenticationExceptionMessageHelper {

  public String buildErrorMessage(AuthenticationException e) {

    if (e instanceof OidcClientError) {
      OidcClientError error = (OidcClientError) e;
      if (error.getError().equals("access_denied")) {
        return "User denied access to requested identity information";
      }
      return error.getError();
    }

    return e.getMessage();
  }

}
