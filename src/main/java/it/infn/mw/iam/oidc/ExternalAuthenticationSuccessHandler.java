package it.infn.mw.iam.oidc;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.http.client.utils.URIBuilder;
import org.mitre.openid.connect.model.OIDCAuthenticationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.DefaultSavedRequest;

import it.infn.mw.iam.oidc.service.OidcUserDetailsService;

public class ExternalAuthenticationSuccessHandler
  extends SavedRequestAwareAuthenticationSuccessHandler {

  public final static String ORIGIN_AUTH_REQUEST_SESSION_VARIABLE = "origin_auth_request";

  protected final static String REDIRECT_URI_SESION_VARIABLE = "redirect_uri";
  protected final static String STATE_SESSION_VARIABLE = "state";
  protected final static String NONCE_SESSION_VARIABLE = "nonce";
  protected final static String ISSUER_SESSION_VARIABLE = "issuer";

  @Autowired
  private OidcUserDetailsService oidcUserDetailService;

  @Autowired
  private Environment env;

  @Override
  public void onAuthenticationSuccess(final HttpServletRequest request,
    final HttpServletResponse response, final Authentication authentication)
    throws IOException, ServletException {

    if (authentication != null && authentication.isAuthenticated()
      && authentication instanceof OIDCAuthenticationToken) {

      OIDCAuthenticationToken auth = (OIDCAuthenticationToken) authentication;

      // get sub & issuer
      String subject = auth.getSub();
      String issuer = auth.getIssuer();

      // find by oidc acccount
      Object o = oidcUserDetailService.loadUserByOIDC(subject, issuer);
      User user = null;

      if (o == null) {
        throw new UsernameNotFoundException(String
          .format("User [%s] not found in IAM database", auth.getPrincipal()));
      } else {
        user = (User) o;
      }

      UsernamePasswordAuthenticationToken userToken = new UsernamePasswordAuthenticationToken(
        user.getUsername(), user.getPassword(), user.getAuthorities());
      SecurityContextHolder.getContext().setAuthentication(userToken);

      String targetUrl = env.getProperty("iam.baseUrl");

      HttpSession session = request.getSession();
      Object obj = session.getAttribute(ORIGIN_AUTH_REQUEST_SESSION_VARIABLE);

      if (obj != null) {

        // redo authz request
        DefaultSavedRequest originAuthRequest = (DefaultSavedRequest) obj;

        Map<String, String> parameters = new HashMap<String, String>();
        for (Map.Entry<String, String[]> entry : originAuthRequest
          .getParameterMap().entrySet()) {
          parameters.put(entry.getKey(), entry.getValue()[0]);
        }

        session.setAttribute("SPRING_SECURITY_SAVED_REQUEST",
          originAuthRequest);
        session.setAttribute(ISSUER_SESSION_VARIABLE, parameters.get("issuer"));
        session.setAttribute(STATE_SESSION_VARIABLE, parameters.get("state"));
        session.setAttribute(REDIRECT_URI_SESION_VARIABLE,
          parameters.get("redirect_uri"));
        session.setAttribute(NONCE_SESSION_VARIABLE, parameters.get("nonce"));

        targetUrl = rebuildAuthzRequestUrl(parameters.get("client_id"),
          parameters.get("scope"), parameters.get("redirect_uri"),
          parameters.get("nonce"), parameters.get("state"), response);

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
      URIBuilder uriBuilder = new URIBuilder(
        env.getProperty("iam.issuer") + "/authorize");
      uriBuilder.addParameter("response_type", "code");
      uriBuilder.addParameter("client_id", clientId);
      uriBuilder.addParameter("scope", scopes);
      uriBuilder.addParameter("redirect_uri", redirectUri);
      uriBuilder.addParameter("nonce", nonce);
      uriBuilder.addParameter("state", state);

      authRequest = uriBuilder.build().toString();

    } catch (URISyntaxException e) {
      throw new AuthenticationServiceException(
        "Malformed Authorization Endpoint Uri", e);
    }

    return authRequest;
  }

}
