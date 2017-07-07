package it.infn.mw.iam.authn.saml;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.providers.ExpiringUsernameAuthenticationToken;
import org.springframework.security.saml.SAMLAuthenticationProvider;
import org.springframework.security.saml.SAMLCredential;

import com.google.common.base.Joiner;

import it.infn.mw.iam.authn.saml.util.SamlUserIdentifierResolutionResult;
import it.infn.mw.iam.authn.saml.util.SamlUserIdentifierResolver;
import it.infn.mw.iam.persistence.model.IamSamlId;

public class IamSamlAuthenticationProvider extends SAMLAuthenticationProvider {

  final SamlUserIdentifierResolver userIdResolver;
  final Joiner joiner = Joiner.on(",").skipNulls();

  public IamSamlAuthenticationProvider(SamlUserIdentifierResolver resolver) {
    this.userIdResolver = resolver;
  }

  private Supplier<AuthenticationServiceException> handleResolutionFailure(
      SamlUserIdentifierResolutionResult result) {
    
    List<String> errorMessages = result.getErrorMessages()
      .orElse(Collections.emptyList());

    return () -> {
      return new AuthenticationServiceException(joiner.join(errorMessages));
    };
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

    SamlUserIdentifierResolutionResult result =
        userIdResolver.resolveSamlUserIdentifier(samlCredentials);

    IamSamlId samlId = result.getResolvedId().orElseThrow(handleResolutionFailure(result));

    SamlExternalAuthenticationToken extAuthnToken =
        new SamlExternalAuthenticationToken(samlId, token, token.getTokenExpiration(),
            user.getUsername(), token.getCredentials(), token.getAuthorities());

    return extAuthnToken;
  }

}
