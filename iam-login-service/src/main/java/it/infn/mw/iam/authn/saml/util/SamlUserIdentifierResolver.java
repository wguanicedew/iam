package it.infn.mw.iam.authn.saml.util;

import org.springframework.security.saml.SAMLCredential;

public interface SamlUserIdentifierResolver {
  public SamlUserIdentifierResolutionResult resolveSamlUserIdentifier(SAMLCredential samlCredential);
}
