package it.infn.mw.iam.authn.saml.util;

import org.springframework.security.saml.SAMLCredential;

import it.infn.mw.iam.persistence.model.IamSamlId;

public class AttributeUserIdentifierResolver extends AbstractSamlUserIdentifierResolver {

  Saml2Attribute attribute;

  public AttributeUserIdentifierResolver(Saml2Attribute attribute) {
    super(attribute.name());
    this.attribute = attribute;
  }

  @Override
  public SamlUserIdentifierResolutionResult resolveSamlUserIdentifier(
      SAMLCredential samlCredential) {

    String attributeValue = samlCredential.getAttributeAsString(attribute.getAttributeName());

    if (attributeValue == null) {
      return SamlUserIdentifierResolutionResult
        .resolutionFailure(String.format("Attribute '%s:%s' not found in assertion", attribute.getAlias(),
            attribute.getAttributeName()));
    }

    IamSamlId samlId = new IamSamlId();
    samlId.setIdpId(samlCredential.getRemoteEntityID());
    samlId.setAttributeId(attribute.getAttributeName());
    samlId.setUserId(attributeValue);

    return SamlUserIdentifierResolutionResult.resolutionSuccess(samlId);

  }

}
