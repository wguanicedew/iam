package it.infn.mw.iam.saml.util;

import org.opensaml.saml2.core.NameID;
import org.springframework.security.saml.SAMLCredential;

import com.google.common.base.Verify;

public class NameIdUserIdentifierAccessor implements SAMLUserIdentifierAccessor {

  @Override
  public String getUserIdentifier(SAMLCredential samlCredential) {

    Verify.verifyNotNull(samlCredential);

    if (samlCredential.getNameID() != null) {
      NameID nameId = samlCredential.getNameID();
      return nameId.getValue();
    }

    return null;
  }

}
