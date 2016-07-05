package it.infn.mw.iam.authn;

import org.springframework.security.core.AuthenticationException;

public interface AuthenticationExceptionMessageHelper {
  
  String buildErrorMessage(AuthenticationException e);

}
