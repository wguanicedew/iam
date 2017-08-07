package it.infn.mw.iam.authn.x509;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

public class IamX509PreauthenticationProcessingFilter
    extends AbstractPreAuthenticatedProcessingFilter {

  public static final Logger LOG =
      LoggerFactory.getLogger(IamX509PreauthenticationProcessingFilter.class);

  public static final String X509_CREDENTIAL_SESSION_KEY = "IAM_X509_CRED";

  private final X509AuthenticationCredentialExtractor credentialExtractor;

  private AuthenticationSuccessHandler successHandler;
  private AuthenticationFailureHandler failureHandler;

  public IamX509PreauthenticationProcessingFilter(X509AuthenticationCredentialExtractor extractor,
      AuthenticationManager authenticationManager) {
    this.credentialExtractor = extractor;
    setCheckForPrincipalChanges(false);
    setAuthenticationManager(authenticationManager);
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


  @Override
  protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {

    Optional<IamX509AuthenticationCredential> credential = extractCredential(request);

    if (!credential.isPresent()) {
      return null;
    }

    final String subject = credential.get().getSubject();

    LOG.debug("Found valid X.509 credential in request with principal subject '{}'", subject);

    return subject;
  }

  @Override
  protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {

    return extractCredential(request).orElse(null);
  }

  @Override
  protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) {

    super.successfulAuthentication(request, response, authentication);

    if (successHandler != null) {
      try {
        successHandler.onAuthenticationSuccess(request, response, authentication);
      } catch (Exception e) {
        throw new RuntimeException(e.getMessage(), e);
      }
    }
  }

  @Override
  protected void unsuccessfulAuthentication(HttpServletRequest request,
      HttpServletResponse response, AuthenticationException failed) {
    
    super.unsuccessfulAuthentication(request, response, failed);

    if (failureHandler != null) {
      try {
        failureHandler.onAuthenticationFailure(request, response, failed);
      } catch (Exception e) {
        throw new RuntimeException(e.getMessage(), e);
      }
    }
  }
  
  public void setSuccessHandler(AuthenticationSuccessHandler successHandler) {
    this.successHandler = successHandler;
  }
  
  public void setFailureHandler(AuthenticationFailureHandler failureHandler) {
    this.failureHandler = failureHandler;
  }

}
