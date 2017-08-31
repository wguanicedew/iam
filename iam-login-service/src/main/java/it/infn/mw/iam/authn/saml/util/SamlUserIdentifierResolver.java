package it.infn.mw.iam.authn.saml.util;

import org.springframework.security.saml.SAMLCredential;
@FunctionalInterface
public interface SamlUserIdentifierResolver {
  public SamlUserIdentifierResolutionResult resolveSamlUserIdentifier(SAMLCredential samlCredential);
}
