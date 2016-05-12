package it.infn.mw.iam.oidc;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.http.client.utils.URIBuilder;
import org.mitre.openid.connect.model.OIDCAuthenticationToken;
import org.mitre.openid.connect.web.AuthenticationTimeStamper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.DefaultSavedRequest;

import it.infn.mw.iam.oidc.service.OidcUserDetailsService;

public class OidcAuthenticationSuccessHandler
  extends SavedRequestAwareAuthenticationSuccessHandler {

  public static final Logger LOG = LoggerFactory
    .getLogger(OidcAuthenticationSuccessHandler.class);

  public final static String ORIGIN_AUTH_REQUEST_SESSION_VARIABLE = "origin_auth_request";

  protected final static String REDIRECT_URI_SESION_VARIABLE = "redirect_uri";
  protected final static String STATE_SESSION_VARIABLE = "state";
  protected final static String NONCE_SESSION_VARIABLE = "nonce";
  protected final static String ISSUER_SESSION_VARIABLE = "issuer";

  @Autowired
  private OidcUserDetailsService oidcUserDetailService;

  @Value("${iam.baseUrl}")
  String iamBaseUrl;

  @Override
  public void onAuthenticationSuccess(final HttpServletRequest request,
    final HttpServletResponse response, final Authentication authentication)
      throws IOException, ServletException {

    if (authentication != null && authentication.isAuthenticated()
      && authentication instanceof OIDCAuthenticationToken) {

      OIDCAuthenticationToken auth = (OIDCAuthenticationToken) authentication;

      User user = null;

      try {
        user = (User) oidcUserDetailService.loadUserByOIDC(auth.getSub(),
          auth.getIssuer());
      } catch (UsernameNotFoundException e) {

        LOG.debug(e.getMessage());

        RequestDispatcher dispatcher = request.getRequestDispatcher("/login");
        request.setAttribute("userNotFoundError",
          String.format("User not found"));

        request.setAttribute("externalAuthenticationToken", auth);

        // Clear authentication
        SecurityContextHolder.getContext().setAuthentication(null);
        dispatcher.forward(request, response);
        return;

      }

      UsernamePasswordAuthenticationToken userToken = new UsernamePasswordAuthenticationToken(
        user.getUsername(), user.getPassword(), user.getAuthorities());

      SecurityContextHolder.getContext().setAuthentication(userToken);

      String targetUrl = iamBaseUrl;

      // Save authentication timestamp in session
      Date timestamp = new Date();
      HttpSession session = request.getSession();
      session.setAttribute(AuthenticationTimeStamper.AUTH_TIMESTAMP,
        new Date());

      LOG.info("Successful OIDC authentication of {} at {}",
        userToken.getName(), timestamp);

      DefaultSavedRequest originAuthRequest = (DefaultSavedRequest) session
        .getAttribute(ORIGIN_AUTH_REQUEST_SESSION_VARIABLE);

      if (originAuthRequest != null) {

        Map<String, String> parameters = new HashMap<String, String>();

        for (Map.Entry<String, String[]> entry : originAuthRequest
          .getParameterMap().entrySet()) {
          parameters.put(entry.getKey(), entry.getValue()[0]);
        }

        session.setAttribute("SPRING_SECURITY_SAVED_REQUEST",
          originAuthRequest);

        if (!parameters.isEmpty()) {

          session.setAttribute(ISSUER_SESSION_VARIABLE,
            parameters.get("issuer"));
          session.setAttribute(STATE_SESSION_VARIABLE, parameters.get("state"));
          session.setAttribute(REDIRECT_URI_SESION_VARIABLE,
            parameters.get("redirect_uri"));
          session.setAttribute(NONCE_SESSION_VARIABLE, parameters.get("nonce"));

          targetUrl = rebuildAuthzRequestUrl(parameters.get("client_id"),
            parameters.get("scope"), parameters.get("redirect_uri"),
            parameters.get("nonce"), parameters.get("state"), response);
        }

      }

      response.sendRedirect(targetUrl);
    }
    return;

  }

  private String rebuildAuthzRequestUrl(String clientId, String scopes,
    String redirectUri, String nonce, String state,
    HttpServletResponse response) throws IOException {

    String authRequest = null;
    try {

      URIBuilder uriBuilder = new URIBuilder(iamBaseUrl + "/authorize");

      uriBuilder.addParameter("response_type", "code");
      uriBuilder.addParameter("client_id", clientId);

      if (scopes != null) {
        uriBuilder.addParameter("scope", scopes);
      }

      uriBuilder.addParameter("redirect_uri", redirectUri);

      if (nonce != null) {
        uriBuilder.addParameter("nonce", nonce);
      }

      uriBuilder.addParameter("state", state);

      authRequest = uriBuilder.build().toString();

    } catch (URISyntaxException e) {
      throw new AuthenticationServiceException(
        "Malformed Authorization Endpoint Uri", e);
    }

    return authRequest;
  }

}
