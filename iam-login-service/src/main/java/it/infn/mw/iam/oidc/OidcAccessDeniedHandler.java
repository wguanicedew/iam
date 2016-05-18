package it.infn.mw.iam.oidc;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

public class OidcAccessDeniedHandler implements AccessDeniedHandler {

  @Override
  public void handle(HttpServletRequest request, HttpServletResponse response,
    AccessDeniedException accessDeniedException)
      throws IOException, ServletException {

    RequestDispatcher dispatcher = request.getRequestDispatcher("/login");
    request.setAttribute("accessDeniedError",
      accessDeniedException.getMessage());

    dispatcher.forward(request, response);

  }

}
