package it.infn.mw.iam.core.util;

import org.springframework.security.core.Authentication;

@FunctionalInterface
public interface AuthenticationLogger {

  public void logAuthenticationSuccess(Authentication auth);

}
