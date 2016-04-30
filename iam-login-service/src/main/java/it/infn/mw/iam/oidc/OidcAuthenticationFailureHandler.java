package it.infn.mw.iam.oidc;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

public class OidcAuthenticationFailureHandler
  implements AuthenticationFailureHandler {

  final OidcExceptionMessageHelper helper;

  public OidcAuthenticationFailureHandler(OidcExceptionMessageHelper h) {
    this.helper = h;
  }

  @Override
  public void onAuthenticationFailure(HttpServletRequest request,
    HttpServletResponse response, AuthenticationException exception)
      throws IOException, ServletException {

    RequestDispatcher dispatcher = request.getRequestDispatcher("/login");
    
    request.setAttribute("externalAuthenticationErrorMessage",
      helper.buildErrorMessage(exception));
    request.setAttribute("externalAuthenticationError", exception);
    
    dispatcher.forward(request, response);

  }

}
