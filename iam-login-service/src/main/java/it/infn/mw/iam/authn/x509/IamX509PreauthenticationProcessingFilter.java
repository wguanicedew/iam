package it.infn.mw.iam.authn.x509;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

public class IamX509PreauthenticationProcessingFilter
    extends AbstractPreAuthenticatedProcessingFilter {

  public static final Logger LOG =
      LoggerFactory.getLogger(IamX509PreauthenticationProcessingFilter.class);

  final X509AuthenticationCredentialExtractor credentialExtractor;

  public IamX509PreauthenticationProcessingFilter(X509AuthenticationCredentialExtractor extractor,
      AuthenticationManager authenticationManager) {
    this.credentialExtractor = extractor;
    setCheckForPrincipalChanges(false);
    setAuthenticationManager(authenticationManager);
  }

  @Override
  protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
    Optional<IamX509AuthenticationCredential> credential =
        credentialExtractor.extractX509Credential(request);

    if (!credential.isPresent()) {
      LOG.debug("No X.509 credential found in request");
      return null;
    }

    final String subject = credential.get().getSubject();

    LOG.debug("Found X.509 credential in request with principal subject '{}'", subject);

    return subject;
  }

  @Override
  protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
    return credentialExtractor.extractX509Credential(request).orElse(null);
  }

}
