package it.infn.mw.iam.saml.util;

import org.springframework.security.saml.SAMLCredential;

public interface SAMLUserIdentifierAccessor {
  
  public String getUserIdentifier(SAMLCredential samlCredential);
  
}
