package it.infn.mw.iam.github;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.web.savedrequest.DefaultSavedRequest;

public class GithubAuthFilter
  extends OAuth2ClientAuthenticationProcessingFilter {

  protected final static String REDIRECT_URI_SESSION_VARIABLE = "redirect_uri";
  protected final static String STATE_SESSION_VARIABLE = "state";
  protected final static String NONCE_SESSION_VARIABLE = "nonce";
  protected final static String ISSUER_SESSION_VARIABLE = "issuer";
  protected static final String TARGET_SESSION_VARIABLE = "target";
  protected final static int HTTP_SOCKET_TIMEOUT = 30000;

  protected final static String ORIGIN_AUTH_REQUEST_SESSION_VARIABLE = "origin_auth_request";

  public final static String FILTER_PROCESSES_URL = "/login/github";

  public GithubAuthFilter() {
    super(FILTER_PROCESSES_URL);
  }

  public GithubAuthFilter(String processingUrl) {
    super(processingUrl);
  }

  @Override
  public Authentication attemptAuthentication(final HttpServletRequest request,
    final HttpServletResponse response)
      throws AuthenticationException, IOException, ServletException {

    HttpSession session = request.getSession();

    // backup origin redirect uri and state
    DefaultSavedRequest savedRequest = (DefaultSavedRequest) session
      .getAttribute("SPRING_SECURITY_SAVED_REQUEST");
    session.setAttribute(ORIGIN_AUTH_REQUEST_SESSION_VARIABLE, savedRequest);

    return super.attemptAuthentication(request, response);

  }

}
