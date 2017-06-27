package it.infn.mw.iam.authn.x509;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;

public class IamX509AuthenticationProvider extends PreAuthenticatedAuthenticationProvider
    implements AuthenticationManager {

  public IamX509AuthenticationProvider() {}

}
