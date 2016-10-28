package it.infn.mw.iam.core.oauth;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.mitre.oauth2.model.ClientDetailsEntity;
import org.mitre.oauth2.model.OAuth2AccessTokenEntity;
import org.mitre.oauth2.service.ClientDetailsEntityService;
import org.mitre.oauth2.service.OAuth2TokenEntityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidRequestException;
import org.springframework.security.oauth2.common.exceptions.InvalidScopeException;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2RequestFactory;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.token.AbstractTokenGranter;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;

public class TokenExchangeTokenGranter extends AbstractTokenGranter {

  public static final Logger LOG = LoggerFactory.getLogger(TokenExchangeTokenGranter.class);

  private static final String GRANT_TYPE = "urn:ietf:params:oauth:grant-type:token-exchange";
  private static final String TOKEN_TYPE = "urn:ietf:params:oauth:token-type:jwt";
  private static final String AUDIENCE_FIELD = "audience";

  /**
   * These scopes, in order to be "exchanged" across services, need to be present in the set of
   * scopes linked to the subject token that is presented for the exchange.
   */
  private static final List<String> CHAINED_SCOPES =
      Arrays.asList("openid", "profile", "email", "address", "phone", "offline_access", "scim:read",
          "scim:write", "registration:read", "registration:write");

  // keep down-cast versions so we can get to the right queries
  private OAuth2TokenEntityService tokenServices;

  @Autowired
  public TokenExchangeTokenGranter(final OAuth2TokenEntityService tokenServices,
      final ClientDetailsEntityService clientDetailsService,
      final OAuth2RequestFactory requestFactory) {
    super(tokenServices, clientDetailsService, requestFactory, GRANT_TYPE);
    this.tokenServices = tokenServices;
  }

  @Override
  protected OAuth2Authentication getOAuth2Authentication(final ClientDetails actorClient,
      final TokenRequest tokenRequest) throws InvalidTokenException {

    if (tokenRequest.getRequestParameters().get("actor_token") != null
        || tokenRequest.getRequestParameters().get("want_composite") != null) {
      throw new InvalidRequestException("Delegation not supported");
    }


    String subjectTokenValue = tokenRequest.getRequestParameters().get("subject_token");
    OAuth2AccessTokenEntity subjectToken = tokenServices.readAccessToken(subjectTokenValue);

    // Does token exchange among clients acting on behalf of themselves make sense?
    if (subjectToken.getAuthenticationHolder().getUserAuth() == null) {
      throw new InvalidRequestException("No user identity linked to subject token.");
    }

    ClientDetailsEntity subjectClient = subjectToken.getClient();

    // check for scoping in the request, can't up-scope with a chained request
    Set<String> requestedScopes = tokenRequest.getScope();
    Set<String> actorScopes = actorClient.getScope();
    Set<String> subjectTokenScopes = subjectToken.getScope();

    // if actor is requesting scopes that is allowed to request, the exchange can happen
    if (actorScopes.containsAll(requestedScopes)) {

      String audience = tokenRequest.getRequestParameters().get(AUDIENCE_FIELD);

      LOG.info(
          "Client '{}' requests token exchange from client '{}' to impersonate user '{}' on audience '{}' with scopes '{}'",
          actorClient.getClientId(), subjectClient.getClientId(),
          subjectToken.getAuthenticationHolder().getUserAuth().getName(), audience,
          requestedScopes);

      // Chained scopes must be among the allowed scopes for the actor client
      // and must also be linked to the subject token
      for (String scope : CHAINED_SCOPES) {
        if (requestedScopes.contains(scope) && !subjectTokenScopes.contains(scope)) {
          throw new InvalidScopeException(
              String.format("Requested scope '%s' not found in subject token", scope));
        }
      }

      tokenRequest.setScope(Sets.intersection(requestedScopes, actorScopes));

      OAuth2Authentication authentication = new OAuth2Authentication(
          getRequestFactory().createOAuth2Request(actorClient, tokenRequest),
          subjectToken.getAuthenticationHolder().getAuthentication().getUserAuthentication());

      if (!Strings.isNullOrEmpty(audience)) {
        authentication.getOAuth2Request().getExtensions().put("aud", audience);
      }

      return authentication;

    } else {
      String errorMsg =
          String.format("Client '%s' requested to exchange a scope that is not allowed to request",
              actorClient.getClientId());

      throw new InvalidScopeException(errorMsg, requestedScopes);
    }
  }

  @Override
  protected OAuth2AccessToken getAccessToken(final ClientDetails client,
      final TokenRequest tokenRequest) {

    OAuth2Authentication auth = getOAuth2Authentication(client, tokenRequest);
    OAuth2AccessToken accessToken = tokenServices.createAccessToken(auth);
    accessToken.getAdditionalInformation().put("issued_token_type", TOKEN_TYPE);

    return accessToken;
  }

}
