package it.infn.mw.iam.authn.saml.util;

import java.util.Optional;

import org.springframework.security.saml.SAMLCredential;

public interface SamlUserIdentifierResolver {
  public Optional<String> getUserIdentifier(SAMLCredential samlCredential);
}
