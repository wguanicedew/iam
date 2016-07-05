package it.infn.mw.iam.authn.saml.util;

import java.util.Optional;

import org.springframework.security.saml.SAMLCredential;

public class AttributeUserIdentifierResolver implements SamlUserIdentifierResolver {

  private final String attributeName;

  public AttributeUserIdentifierResolver(String attributeName) {
    this.attributeName = attributeName;
  }

  @Override
  public Optional<String> getUserIdentifier(SAMLCredential samlCredential) {
    
    return Optional.ofNullable(samlCredential.getAttributeAsString(attributeName));
    
  }

}
