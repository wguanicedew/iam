package it.infn.mw.iam.api.tokens.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.mitre.oauth2.model.OAuth2AccessTokenEntity;
import org.mitre.oauth2.service.impl.DefaultOAuth2ProviderTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import it.infn.mw.iam.api.tokens.converter.TokensConverter;
import it.infn.mw.iam.api.tokens.exception.TokenNotFoundException;
import it.infn.mw.iam.api.tokens.model.AccessToken;
import it.infn.mw.iam.api.tokens.model.TokensListResponse;
import it.infn.mw.iam.api.tokens.service.paging.OffsetPageable;
import it.infn.mw.iam.api.tokens.service.paging.TokensPageRequest;
import it.infn.mw.iam.persistence.repository.IamOAuthAccessTokenRepository;

@Service
public class DefaultAccessTokenService implements TokenService<AccessToken> {

  @Autowired
  private TokensConverter tokensConverter;

  @Autowired
  private DefaultOAuth2ProviderTokenService tokenService;

  @Autowired
  private IamOAuthAccessTokenRepository tokenRepository;

  @Override
  public AccessToken getTokenById(Long id) {

    OAuth2AccessTokenEntity at =
        getAccessTokenById(id).orElseThrow(() -> new TokenNotFoundException(id));
    return tokensConverter.toAccessToken(at);
  }

  @Override
  public void revokeTokenById(Long id) {

    OAuth2AccessTokenEntity at =
        getAccessTokenById(id).orElseThrow(() -> new TokenNotFoundException(id));
    tokenService.revokeAccessToken(at);
  }

  private Optional<OAuth2AccessTokenEntity> getAccessTokenById(Long accessTokenId) {

    OAuth2AccessTokenEntity at = tokenService.getAccessTokenById(accessTokenId);
    return Optional.ofNullable(at);
  }

  private Page<OAuth2AccessTokenEntity> getAllValidTokens(OffsetPageable op) {
    return tokenRepository.findAllValidAccessTokens(new Date(), op);
  }

  private Page<OAuth2AccessTokenEntity> getAllValidTokensForUser(String userId, OffsetPageable op) {
    return tokenRepository.findValidAccessTokensForUser(userId, new Date(), op);
  }

  private Page<OAuth2AccessTokenEntity> getAllValidTokensForClient(String clientId,
      OffsetPageable op) {
    return tokenRepository.findValidAccessTokensForClient(clientId, new Date(), op);
  }

  private Page<OAuth2AccessTokenEntity> getAllValidTokensForUserAndClient(String userId,
      String clientId, OffsetPageable op) {
    return tokenRepository.findValidAccessTokensForUserAndClient(userId, clientId, new Date(), op);
  }

  private TokensListResponse<AccessToken> buildResponse(TokensPageRequest pageRequest,
      Page<OAuth2AccessTokenEntity> entities) {

    if (pageRequest.getCount() == 0) {
      return new TokensListResponse<>(Collections.emptyList(), entities.getTotalElements(), 0, 1);
    }

    List<AccessToken> resources = new ArrayList<>();

    entities.getContent().forEach(a -> resources.add(tokensConverter.toAccessToken(a)));

    return new TokensListResponse<>(resources, entities.getTotalElements(), resources.size(),
        pageRequest.getStartIndex() + 1);
  }

  private OffsetPageable getOffsetPageable(TokensPageRequest pageRequest) {

    if (pageRequest.getCount() == 0) {
      return new OffsetPageable(0, 1);
    }
    return new OffsetPageable(pageRequest.getStartIndex(), pageRequest.getCount());
  }

  @Override
  public TokensListResponse<AccessToken> getAllTokens(TokensPageRequest pageRequest) {

    return buildResponse(pageRequest, getAllValidTokens(getOffsetPageable(pageRequest)));
  }

  @Override
  public TokensListResponse<AccessToken> getTokensForUser(String userId,
      TokensPageRequest pageRequest) {

    return buildResponse(pageRequest,
        getAllValidTokensForUser(userId, getOffsetPageable(pageRequest)));
  }

  @Override
  public TokensListResponse<AccessToken> getTokensForClient(String clientId,
      TokensPageRequest pageRequest) {

    return buildResponse(pageRequest,
        getAllValidTokensForClient(clientId, getOffsetPageable(pageRequest)));
  }

  @Override
  public TokensListResponse<AccessToken> getTokensForClientAndUser(String userId, String clientId,
      TokensPageRequest pageRequest) {

    return buildResponse(pageRequest,
        getAllValidTokensForUserAndClient(userId, clientId, getOffsetPageable(pageRequest)));
  }

  @Override
  public void deleteAllTokens() {
    tokenRepository.deleteAll();
  }
}
