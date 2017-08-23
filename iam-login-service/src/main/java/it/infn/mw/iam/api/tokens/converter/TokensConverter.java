package it.infn.mw.iam.api.tokens.converter;

import it.infn.mw.iam.api.scim.converter.ScimResourceLocationProvider;
import it.infn.mw.iam.api.tokens.TokensResourceLocationProvider;
import it.infn.mw.iam.api.tokens.model.AccessToken;
import it.infn.mw.iam.api.tokens.model.ClientRef;
import it.infn.mw.iam.api.tokens.model.IdTokenRef;
import it.infn.mw.iam.api.tokens.model.RefreshToken;
import it.infn.mw.iam.api.tokens.model.UserRef;
import it.infn.mw.iam.core.user.exception.IamAccountException;
import it.infn.mw.iam.persistence.model.IamAccount;
import it.infn.mw.iam.persistence.repository.IamAccountRepository;

import org.mitre.oauth2.model.AuthenticationHolderEntity;
import org.mitre.oauth2.model.ClientDetailsEntity;
import org.mitre.oauth2.model.OAuth2AccessTokenEntity;
import org.mitre.oauth2.model.OAuth2RefreshTokenEntity;
import org.mitre.oauth2.model.SavedUserAuthentication;
import org.mitre.oauth2.service.ClientDetailsEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TokensConverter {

  @Autowired
  private ClientDetailsEntityService clientDetailsService;

  @Autowired
  private IamAccountRepository accountRepository;

  @Autowired
  private TokensResourceLocationProvider tokensResourceLocationProvider;

  @Autowired
  private ScimResourceLocationProvider scimResourceLocationProvider;

  public AccessToken toAccessToken(OAuth2AccessTokenEntity at) {

    AuthenticationHolderEntity ah = at.getAuthenticationHolder();
    ClientDetailsEntity cd = clientDetailsService.loadClientByClientId(ah.getClientId());

    UserRef userRef = null;

    if (hasUser(at)) {
      userRef = buildUserRef(getUser(at));
    }

    IdTokenRef idTokenRef = null;

    if (hasIdToken(at)) {
      idTokenRef = buildIdTokenRef(getIdToken(at));
    }

    return AccessToken.builder()
        .id(at.getId())
        .client(ClientRef.builder()
            .id(cd.getId())
            .clientId(cd.getClientId())
            .contacts(cd.getContacts())
            .ref(cd.getClientUri())
            .build())
        .expiration(at.getExpiration())
        .idToken(idTokenRef)
        .scopes(at.getScope())
        .user(userRef)
        .value(at.getValue())
        .build();
  }

  public RefreshToken toRefreshToken(OAuth2RefreshTokenEntity rt) {

    AuthenticationHolderEntity ah = rt.getAuthenticationHolder();
    ClientDetailsEntity cd = clientDetailsService.loadClientByClientId(ah.getClientId());

    UserRef userRef = null;

    if (hasUser(rt)) {
      userRef = buildUserRef(getUser(rt));
    }

    return RefreshToken.builder()
        .id(rt.getId())
        .client(ClientRef.builder()
            .id(cd.getId())
            .clientId(cd.getClientId())
            .contacts(cd.getContacts())
            .ref(cd.getClientUri())
            .build())
        .expiration(rt.getExpiration())
        .user(userRef)
        .value(rt.getValue())
        .build();
  }

  private boolean hasUser(OAuth2AccessTokenEntity at) {

    return getUser(at) instanceof SavedUserAuthentication;
  }

  private SavedUserAuthentication getUser(OAuth2AccessTokenEntity at) {

    return at.getAuthenticationHolder().getUserAuth();
  }

  private boolean hasUser(OAuth2RefreshTokenEntity rt) {

    return getUser(rt) instanceof SavedUserAuthentication;
  }

  private SavedUserAuthentication getUser(OAuth2RefreshTokenEntity rt) {

    return rt.getAuthenticationHolder().getUserAuth();
  }

  private boolean hasIdToken(OAuth2AccessTokenEntity at) {

    return getIdToken(at) != null;
  }

  private OAuth2AccessTokenEntity getIdToken(OAuth2AccessTokenEntity at) {

    return at.getIdToken();
  }

  private UserRef buildUserRef(SavedUserAuthentication userAuth) {

    String username = userAuth.getPrincipal().toString();

    IamAccount account = accountRepository.findByUsername(username)
        .orElseThrow(() -> new IamAccountException("Account for " + username + " not found"));

    return UserRef.builder()
        .id(account.getUuid())
        .userName(account.getUsername())
        .ref(scimResourceLocationProvider.userLocation(account.getUuid()))
        .build();
  }

  private IdTokenRef buildIdTokenRef(OAuth2AccessTokenEntity idToken) {

    Long id = idToken.getId();
    String ref = tokensResourceLocationProvider.accessTokenLocation(id);

    return IdTokenRef.builder().id(id).ref(ref).build();
  }
}
