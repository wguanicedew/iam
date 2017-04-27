package it.infn.mw.iam.authn.saml.util;

import java.util.Optional;

import org.springframework.security.saml.SAMLCredential;

public class AttributeUserIdentifierResolver extends AbstractSamlUserIdentifierResolver {

  Saml2Attribute attribute;

  public AttributeUserIdentifierResolver(Saml2Attribute attribute) {
    super(attribute.name());
    this.attribute = attribute;
  }

  @Override
  public Optional<String> getUserIdentifier(SAMLCredential samlCredential) {

    return Optional.ofNullable(samlCredential.getAttributeAsString(attribute.getAttributeName()));
  }

}
