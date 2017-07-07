package it.infn.mw.iam.authn.saml;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.saml.SAMLCredential;

import it.infn.mw.iam.authn.ExternalAuthenticationHandlerSupport;
import it.infn.mw.iam.authn.InactiveAccountAuthenticationHander;
import it.infn.mw.iam.authn.saml.util.SamlUserIdentifierResolver;
import it.infn.mw.iam.persistence.model.IamAccount;
import it.infn.mw.iam.persistence.model.IamAuthority;
import it.infn.mw.iam.persistence.model.IamSamlId;

public class SAMLUserDetailsServiceSupport {

  private final InactiveAccountAuthenticationHander inactiveAccountHandler;
  private final SamlUserIdentifierResolver resolver;

  protected SAMLUserDetailsServiceSupport(InactiveAccountAuthenticationHander inactiveAccountHandler,
      SamlUserIdentifierResolver resolver) {
    
    this.inactiveAccountHandler = inactiveAccountHandler;
    this.resolver = resolver;
  }

  protected List<GrantedAuthority> convertAuthorities(IamAccount a) {

    List<GrantedAuthority> authorities = new ArrayList<>();
    for (IamAuthority auth : a.getAuthorities()) {
      authorities.add(new SimpleGrantedAuthority(auth.getAuthority()));
    }
    return authorities;
  }

  protected User buildUserFromIamAccount(IamAccount account) {
    inactiveAccountHandler.handleInactiveAccount(account);
    return new User(account.getUsername(), account.getPassword(), convertAuthorities(account));
  }

  protected User buildUserFromSamlCredential(IamSamlId samlId, SAMLCredential credential) {
    return new User(samlId.getUserId(), "",
        Arrays.asList(ExternalAuthenticationHandlerSupport.EXT_AUTHN_UNREGISTERED_USER_AUTH));
  }

  protected IamSamlId resolverSamlId(SAMLCredential credential) {
    return resolver.resolveSamlUserIdentifier(credential).getResolvedId()
      .orElseThrow(() -> new UsernameNotFoundException(
          "Could not extract a user identifier from the SAML assertion"));
  }
}
