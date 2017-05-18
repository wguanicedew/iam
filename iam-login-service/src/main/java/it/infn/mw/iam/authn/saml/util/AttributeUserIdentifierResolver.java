package it.infn.mw.iam.authn.saml.util;

import java.util.Optional;

import org.springframework.security.saml.SAMLCredential;

import it.infn.mw.iam.persistence.model.IamSamlId;

public class AttributeUserIdentifierResolver extends AbstractSamlUserIdentifierResolver {

  Saml2Attribute attribute;

  public AttributeUserIdentifierResolver(Saml2Attribute attribute) {
    super(attribute.name());
    this.attribute = attribute;
  }

  @Override
  public Optional<IamSamlId> getSamlUserIdentifier(SAMLCredential samlCredential) {

    String attributeValue = samlCredential.getAttributeAsString(attribute.getAttributeName());

    if (attributeValue == null) {
      return Optional.empty();
    }

    IamSamlId samlId = new IamSamlId();
    samlId.setIdpId(samlCredential.getRemoteEntityID());
    samlId.setAttributeId(attribute.getAttributeName());
    samlId.setUserId(attributeValue);

    return Optional.of(samlId);

  }

}
