package it.infn.mw.iam.authn.saml.util;

import static it.infn.mw.iam.authn.saml.util.SamlUserIdentifierResolutionResult.resolutionFailure;
import static java.lang.String.format;
import static java.util.Objects.isNull;

import org.opensaml.saml2.core.Attribute;
import org.opensaml.saml2.core.NameID;
import org.opensaml.saml2.core.NameIDType;
import org.opensaml.xml.XMLObject;
import org.springframework.security.saml.SAMLCredential;

import it.infn.mw.iam.persistence.model.IamSamlId;

public class EPTIDUserIdentifierResolver extends AttributeUserIdentifierResolver {

  public EPTIDUserIdentifierResolver() {
    super(Saml2Attribute.EPTID);
  }

  @Override
  public SamlUserIdentifierResolutionResult resolveSamlUserIdentifier(
      SAMLCredential samlCredential) {

    Attribute eptidAttr = samlCredential.getAttribute(attribute.getAttributeName());

    if (eptidAttr == null) {
      return SamlUserIdentifierResolutionResult
        .resolutionFailure(format("Attribute '%s:%s' not found in assertion", attribute.getAlias(),
            attribute.getAttributeName()));
    }

    if (isNull(eptidAttr.getAttributeValues()) ) {
      return SamlUserIdentifierResolutionResult
          .resolutionFailure(format("Attribute '%s:%s' is malformed: null or empty list of values",
              attribute.getAlias(), attribute.getAttributeName()));
    }
    
    if (eptidAttr.getAttributeValues().isEmpty()) {
      return SamlUserIdentifierResolutionResult
        .resolutionFailure(format("Attribute '%s:%s' is malformed: null or empty list of values",
            attribute.getAlias(), attribute.getAttributeName()));
    }

    if (eptidAttr.getAttributeValues().size() > 1) {
      return SamlUserIdentifierResolutionResult
        .resolutionFailure(format("Attribute '%s:%s' is malformed: more than one value found",
            attribute.getAlias(), attribute.getAttributeName()));
    }

    XMLObject maybeNameId = eptidAttr.getAttributeValues().get(0);

    if (!(maybeNameId instanceof NameID)) {
      return SamlUserIdentifierResolutionResult
        .resolutionFailure(format("Attribute '%s:%s' is malformed: value is not a NameID",
            attribute.getAlias(), attribute.getAttributeName()));
    }

    NameID nameId = (NameID) maybeNameId;
    if (!nameId.getFormat().equals(NameIDType.PERSISTENT)) {
      return resolutionFailure(
          format("Attribute '%s:%s' is malformed: resolved NameID is not persistent: %s",
              attribute.getAlias(), attribute.getAttributeName(), nameId.getFormat()));
    }

    IamSamlId samlId = new IamSamlId();
    samlId.setIdpId(samlCredential.getRemoteEntityID());
    samlId.setIdpId(nameId.getNameQualifier());
    samlId.setAttributeId(attribute.getAttributeName());
    samlId.setUserId(nameId.getValue());

    return SamlUserIdentifierResolutionResult.resolutionSuccess(samlId);
  }
}
