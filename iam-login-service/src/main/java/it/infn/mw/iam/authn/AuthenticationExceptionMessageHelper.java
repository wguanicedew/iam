package it.infn.mw.iam.authn;

import org.springframework.security.core.AuthenticationException;

@FunctionalInterface
public interface AuthenticationExceptionMessageHelper {
  
  String buildErrorMessage(AuthenticationException e);

}
