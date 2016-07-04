package it.infn.mw.iam.authn;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.web.util.UriComponentsBuilder;

public class ExternalAuthenticationFailureHandler implements AuthenticationFailureHandler {

  private static final Logger LOG =
      LoggerFactory.getLogger(ExternalAuthenticationFailureHandler.class);

  private final AuthenticationExceptionMessageHelper helper;

  public ExternalAuthenticationFailureHandler(AuthenticationExceptionMessageHelper errorHelper) {
    this.helper = errorHelper;
  }


  private String buildRedirectURL(AuthenticationException exception) {
    return UriComponentsBuilder.fromPath("/login")
      .queryParam("error", "true")
      .queryParam("externalAuthenticationError", helper.buildErrorMessage(exception))
      .build()
      .toString();
  }

  @Override
  public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
      AuthenticationException exception) throws IOException, ServletException {


    LOG.info("External authentication failure: {}", exception.getMessage());

    response.sendRedirect(buildRedirectURL(exception));


  }

}
