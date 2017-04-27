package it.infn.mw.iam.authn.saml.util;

import java.util.Optional;

import org.opensaml.saml2.core.NameID;
import org.springframework.security.saml.SAMLCredential;

import com.google.common.base.Verify;

public class NameIdUserIdentifierResolver extends AbstractSamlUserIdentifierResolver{

  public static final String NAMEID_RESOLVER = "nameID";
  
  public NameIdUserIdentifierResolver() {
    super(NAMEID_RESOLVER);
  }

  @Override
  public Optional<String> getUserIdentifier(SAMLCredential samlCredential) {

    Verify.verifyNotNull(samlCredential);

    if (samlCredential.getNameID() != null) {
      NameID nameId = samlCredential.getNameID();
      return Optional.of(nameId.getValue());

    }
    
    return Optional.empty();
  }

}
