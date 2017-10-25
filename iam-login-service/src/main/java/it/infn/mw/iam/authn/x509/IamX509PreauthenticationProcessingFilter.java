package it.infn.mw.iam.authn.x509;

import java.io.IOException;
import java.util.Optional;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

public class IamX509PreauthenticationProcessingFilter
    extends AbstractPreAuthenticatedProcessingFilter {

  public static final Logger LOG =
      LoggerFactory.getLogger(IamX509PreauthenticationProcessingFilter.class);

  public static final String X509_CREDENTIAL_SESSION_KEY = "IAM_X509_CRED";
  public static final String X509_ERROR_KEY = "IAM_X509_AUTHN_ERROR";
  public static final String X509_CAN_LOGIN_KEY = "IAM_X509_CAN_LOGIN";
  
  public static final String X509_AUTHN_REQUESTED_PARAM = "x509ClientAuth";
  
  private final X509AuthenticationCredentialExtractor credentialExtractor;
  
  private final AuthenticationSuccessHandler successHandler;

  public IamX509PreauthenticationProcessingFilter(X509AuthenticationCredentialExtractor extractor,
      AuthenticationManager authenticationManager, AuthenticationSuccessHandler successHandler) {
    setCheckForPrincipalChanges(false);
    setAuthenticationManager(authenticationManager);
    this.credentialExtractor = extractor;
    this.successHandler = successHandler;
  }

  protected boolean x509AuthenticationRequested(HttpServletRequest request) {
    return (request.getParameter(X509_AUTHN_REQUESTED_PARAM) != null);
  }

  protected void storeCredentialInSession(HttpServletRequest request,
      IamX509AuthenticationCredential cred) {

    HttpSession session = request.getSession(false);

    if (session != null && !cred.failedVerification()) {
      LOG.debug("Storing X.509 {} credential in session ", cred);
      session.setAttribute(X509_CREDENTIAL_SESSION_KEY, cred);
    }

  }

  protected Optional<IamX509AuthenticationCredential> extractCredential(
      HttpServletRequest request) {
    Optional<IamX509AuthenticationCredential> credential =
        credentialExtractor.extractX509Credential(request);

    if (!credential.isPresent()) {
      LOG.debug("No X.509 client credential found in request");
    }

    if (credential.isPresent() && credential.get().failedVerification()) {
      LOG.warn("X.509 client credential failed verification: {}",
          credential.get().verificationError());
      return Optional.empty();
    }

    credential.ifPresent(c -> storeCredentialInSession(request, c));

    return credential;
  }

  protected void logX509CredentialInfo(IamX509AuthenticationCredential cred) {
    LOG.debug("Found valid X.509 credential in request with principal subject '{}'",
        cred.getSubject());
  }

  @Override
  protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {

    Optional<IamX509AuthenticationCredential> credential = extractCredential(request);

    if (!credential.isPresent()) {
      return null;
    }

    credential.ifPresent(this::logX509CredentialInfo);
    return credential.get().getSubject();
  }

  @Override
  protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {

    return extractCredential(request).orElse(null);
  }

  @Override
  protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) {

    request.setAttribute(X509_CAN_LOGIN_KEY, Boolean.TRUE);
    
    if (x509AuthenticationRequested(request)) {
      super.successfulAuthentication(request, response, authentication);
      
      try {
        successHandler.onAuthenticationSuccess(request, response, authentication);
        
      } catch (IOException | ServletException e) {
        throw new X509AuthenticationError(e);
      }
    }
  }

  @Override
  protected void unsuccessfulAuthentication(HttpServletRequest request,
      HttpServletResponse response, AuthenticationException failed) {

    super.unsuccessfulAuthentication(request, response, failed);
  }
  
}
