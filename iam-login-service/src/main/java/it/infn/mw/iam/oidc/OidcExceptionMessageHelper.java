package it.infn.mw.iam.oidc;

import org.springframework.security.core.AuthenticationException;

public class OidcExceptionMessageHelper {

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
