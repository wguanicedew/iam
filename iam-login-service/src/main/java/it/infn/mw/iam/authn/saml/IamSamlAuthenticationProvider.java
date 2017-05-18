package it.infn.mw.iam.authn.saml;

import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.providers.ExpiringUsernameAuthenticationToken;
import org.springframework.security.saml.SAMLAuthenticationProvider;
import org.springframework.security.saml.SAMLCredential;

import it.infn.mw.iam.authn.saml.util.SamlUserIdentifierResolver;
import it.infn.mw.iam.persistence.model.IamSamlId;

public class IamSamlAuthenticationProvider extends SAMLAuthenticationProvider {

  final SamlUserIdentifierResolver userIdResolver;

  public IamSamlAuthenticationProvider(SamlUserIdentifierResolver resolver) {
    this.userIdResolver = resolver;
  }

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {

    ExpiringUsernameAuthenticationToken token =
        (ExpiringUsernameAuthenticationToken) super.authenticate(authentication);

    if (token == null) {
      return null;
    }

    User user = (User) token.getDetails();

    SAMLCredential samlCredentials = (SAMLCredential) token.getCredentials();

    IamSamlId samlId = userIdResolver.getSamlUserIdentifier(samlCredentials)
      .orElseThrow(() -> new AuthenticationServiceException(
          "Could not resolve user identifier from SAML assertion"));

    SamlExternalAuthenticationToken extAuthnToken =
        new SamlExternalAuthenticationToken(samlId, token, token.getTokenExpiration(), user.getUsername(),
            token.getCredentials(), token.getAuthorities());

    return extAuthnToken;
  }

}
