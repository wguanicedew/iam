package it.infn.mw.iam.authn.saml.util;

import static it.infn.mw.iam.authn.saml.util.SamlUserIdentifierResolutionResult.resolutionFailure;
import static it.infn.mw.iam.authn.saml.util.SamlUserIdentifierResolutionResult.resolutionSuccess;
import static java.util.Objects.isNull;

import org.opensaml.saml2.core.NameID;
import org.opensaml.saml2.core.NameIDType;
import org.springframework.security.saml.SAMLCredential;

import it.infn.mw.iam.persistence.model.IamSamlId;

public class PersistentNameIdUserIdentifierResolver extends AbstractSamlUserIdentifierResolver {

  public static final String PERSISTENT_NAMEID_RESOLVER = "persistentNameID";

  public PersistentNameIdUserIdentifierResolver() {
    super(PERSISTENT_NAMEID_RESOLVER);
  }

  @Override
  public SamlUserIdentifierResolutionResult resolveSamlUserIdentifier(
      SAMLCredential samlCredential) {

    if (isNull(samlCredential.getNameID())) {
      return resolutionFailure(
          "PersistentNameID resolution failure: NameID element not found in samlAssertion");
    }

    NameID nameId = samlCredential.getNameID();

    if (!nameId.getFormat().equals(NameIDType.PERSISTENT)) {
      return resolutionFailure(
          "PersistentNameID resolution failure: resolved NameID is not persistent: "+nameId.getFormat());
    }
    
    IamSamlId samlId = new IamSamlId();
    samlId.setAttributeId(nameId.getFormat());
    samlId.setUserId(nameId.getValue());
    samlId.setIdpId(samlCredential.getRemoteEntityID());

    return resolutionSuccess(samlId);
  }
}
