package it.infn.mw.iam.authn;

import java.util.Collection;
import java.util.Date;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.providers.ExpiringUsernameAuthenticationToken;

public class ExternalAuthenticationToken extends ExpiringUsernameAuthenticationToken {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;


  private Authentication externalAuthentication;

  public ExternalAuthenticationToken(Authentication externalAuthentication, Object principal,
      Object credentials) {
    super(principal, credentials);
    this.externalAuthentication = externalAuthentication;
  }

  public ExternalAuthenticationToken(Authentication externalAuthentication, Date tokenExpiration,
      Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
    super(tokenExpiration, principal, credentials, authorities);
    this.externalAuthentication = externalAuthentication;
  }

  public Authentication getExternalAuthentication() {
    return externalAuthentication;
  }

}
