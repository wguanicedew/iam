package it.infn.mw.iam.authn.saml.util;

import java.util.Optional;

import org.springframework.security.saml.SAMLCredential;

import it.infn.mw.iam.persistence.model.IamSamlId;

public interface SamlUserIdentifierResolver {
  public Optional<IamSamlId> getSamlUserIdentifier(SAMLCredential samlCredential);
}
