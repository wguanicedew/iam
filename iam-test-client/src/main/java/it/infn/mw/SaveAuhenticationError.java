package it.infn.mw;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;


public class SaveAuhenticationError implements AuthenticationFailureHandler {

  public SaveAuhenticationError() {
    // TODO Auto-generated constructor stub
  }

  @Override
  public void onAuthenticationFailure(HttpServletRequest request,
    HttpServletResponse response, AuthenticationException exception)
      throws IOException, ServletException {

    request.setAttribute("authnException", exception);
    
    RequestDispatcher dispatcher = request.getRequestDispatcher("/error");
    dispatcher.forward(request, response);
    

  }

}
