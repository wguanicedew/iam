package it.infn.mw.iam.api.account_linking;

import java.security.Principal;

import it.infn.mw.iam.authn.AbstractExternalAuthenticationToken;
import it.infn.mw.iam.authn.ExternalAuthenticationRegistrationInfo.ExternalAuthenticationType;
import it.infn.mw.iam.authn.x509.IamX509AuthenticationCredential;

public interface AccountLinkingService {

  void linkX509Certificate(Principal authenticatedUser,
      IamX509AuthenticationCredential x509Credential);

  void unlinkX509Certificate(Principal authenticatedUser, String certificateSubject);

  void linkExternalAccount(Principal authenticatedUser,
      AbstractExternalAuthenticationToken<?> externalAuthenticationToken);

  void unlinkExternalAccount(Principal authenticatedUser, ExternalAuthenticationType type,
      String iss, String sub, String attributeId);

}
