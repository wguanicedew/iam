package it.infn.mw.iam.authn.saml;

import org.springframework.security.core.AuthenticationException;

import it.infn.mw.iam.authn.AuthenticationExceptionMessageHelper;

public class SamlExceptionMessageHelper implements AuthenticationExceptionMessageHelper {

  @Override
  public String buildErrorMessage(AuthenticationException e) {
    return e.getMessage();
  }

}
