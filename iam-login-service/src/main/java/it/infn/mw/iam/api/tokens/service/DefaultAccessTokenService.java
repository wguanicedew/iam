package it.infn.mw.iam.api.tokens.service;

import it.infn.mw.iam.api.tokens.converter.TokensConverter;
import it.infn.mw.iam.api.tokens.exception.TokenNotFoundException;
import it.infn.mw.iam.api.tokens.model.AccessToken;
import it.infn.mw.iam.api.tokens.model.TokensListResponse;
import it.infn.mw.iam.api.tokens.service.paging.OffsetPageable;
import it.infn.mw.iam.api.tokens.service.paging.TokensPageRequest;
import it.infn.mw.iam.persistence.repository.IamOAuthAccessTokenRepository;

import org.mitre.oauth2.model.OAuth2AccessTokenEntity;
import org.mitre.oauth2.service.impl.DefaultOAuth2ProviderTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

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

  private TokensListResponse<AccessToken> getCountZeroList() {

    int tokenCount = tokenRepository.countAllTokens();
    return new TokensListResponse<>(Collections.emptyList(), tokenCount, 0, 1);
  }

  private TokensListResponse<AccessToken> buildTokensListResponse(
      Page<OAuth2AccessTokenEntity> entities, OffsetPageable op) {

    List<AccessToken> resources = new ArrayList<>();

    entities.getContent().forEach(a -> resources.add(tokensConverter.toAccessToken(a)));

    return new TokensListResponse<>(resources, entities.getTotalElements(), resources.size(),
        op.getOffset() + 1);
  }

  @Override
  public TokensListResponse<AccessToken> getAllTokens(TokensPageRequest pageRequest) {

    if (pageRequest.getCount() == 0) {
      return getCountZeroList();
    }

    OffsetPageable op = new OffsetPageable(pageRequest.getStartIndex(), pageRequest.getCount());
    return buildTokensListResponse(tokenRepository.findAllValidAccessTokens(new Date(), op), op);
  }

  @Override
  public TokensListResponse<AccessToken> getTokensForUser(String userId,
      TokensPageRequest pageRequest) {

    if (pageRequest.getCount() == 0) {
      return getCountZeroList();
    }

    OffsetPageable op = new OffsetPageable(pageRequest.getStartIndex(), pageRequest.getCount());
    return buildTokensListResponse(
        tokenRepository.findValidAccessTokensForUser(userId, new Date(), op), op);
  }

  @Override
  public TokensListResponse<AccessToken> getTokensForClient(String clientId,
      TokensPageRequest pageRequest) {

    if (pageRequest.getCount() == 0) {
      return getCountZeroList();
    }

    OffsetPageable op = new OffsetPageable(pageRequest.getStartIndex(), pageRequest.getCount());
    return buildTokensListResponse(
        tokenRepository.findValidAccessTokensForClient(clientId, new Date(), op), op);
  }

  @Override
  public TokensListResponse<AccessToken> getTokensForClientAndUser(String userId, String clientId,
      TokensPageRequest pageRequest) {

    if (pageRequest.getCount() == 0) {
      return getCountZeroList();
    }

    OffsetPageable op = new OffsetPageable(pageRequest.getStartIndex(), pageRequest.getCount());
    return buildTokensListResponse(
        tokenRepository.findValidAccessTokensForUserAndClient(userId, clientId, new Date(), op),
        op);
  }
}
