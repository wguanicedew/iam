package it.infn.mw.iam.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

public enum IamAuthenticationLogger implements AuthenticationLogger {

  INSTANCE;

  private final Logger log = LoggerFactory.getLogger(IamAuthenticationLogger.class);

  @Override
  public void logAuthenticationSuccess(Authentication auth) {
    if (!(auth instanceof OAuth2Authentication)) {
      log.info("{} was authenticated succesfully", auth.getName());
      return;
    }

    final OAuth2Authentication oauth = (OAuth2Authentication) auth;
    final String clientName = oauth.getName();

    if (oauth.getUserAuthentication() != null) {
      final String userName = oauth.getUserAuthentication().getName();
      log.info("{} acting for {} was authenticated succesfully", clientName, userName);
    } else {
      log.info("Client {} was authenticated succesfully", clientName);
    }
  }

}
