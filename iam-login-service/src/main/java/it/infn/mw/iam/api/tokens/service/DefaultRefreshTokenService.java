package it.infn.mw.iam.api.tokens.service;

import it.infn.mw.iam.api.tokens.converter.TokensConverter;
import it.infn.mw.iam.api.tokens.exception.TokenNotFoundException;
import it.infn.mw.iam.api.tokens.model.RefreshToken;
import it.infn.mw.iam.api.tokens.model.TokensListResponse;
import it.infn.mw.iam.api.tokens.service.paging.OffsetPageable;
import it.infn.mw.iam.api.tokens.service.paging.TokensPageRequest;
import it.infn.mw.iam.persistence.repository.IamOAuthRefreshTokenRepository;

import org.mitre.oauth2.model.OAuth2RefreshTokenEntity;
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
public class DefaultRefreshTokenService implements TokenService<RefreshToken> {

  @Autowired
  private TokensConverter tokensConverter;

  @Autowired
  private DefaultOAuth2ProviderTokenService tokenService;

  @Autowired
  private IamOAuthRefreshTokenRepository tokenRepository;

  @Override
  public RefreshToken getTokenById(Long id) {

    OAuth2RefreshTokenEntity rt =
        getRefreshTokenById(id).orElseThrow(() -> new TokenNotFoundException(id));
    return tokensConverter.toRefreshToken(rt);
  }

  @Override
  public void revokeTokenById(Long id) {

    OAuth2RefreshTokenEntity rt =
        getRefreshTokenById(id).orElseThrow(() -> new TokenNotFoundException(id));
    tokenService.revokeRefreshToken(rt);
  }

  private Optional<OAuth2RefreshTokenEntity> getRefreshTokenById(Long refreshTokenId) {

    OAuth2RefreshTokenEntity at = tokenService.getRefreshTokenById(refreshTokenId);
    return Optional.ofNullable(at);
  }

  private TokensListResponse<RefreshToken> buildTokensCountResponse() {

    int tokenCount = tokenRepository.countAllTokens();
    return new TokensListResponse<>(Collections.emptyList(), tokenCount, 0, 1);
  }

  private TokensListResponse<RefreshToken> buildTokensListResponse(
      Page<OAuth2RefreshTokenEntity> entities, OffsetPageable op) {

    List<RefreshToken> resources = new ArrayList<>();

    entities.getContent().forEach(a -> resources.add(tokensConverter.toRefreshToken(a)));

    return new TokensListResponse<>(resources, entities.getTotalElements(), resources.size(),
        op.getOffset() + 1);
  }

  @Override
  public TokensListResponse<RefreshToken> getAllTokens(TokensPageRequest pageRequest) {

    if (pageRequest.getCount() == 0) {
      return buildTokensCountResponse();
    }

    OffsetPageable op = new OffsetPageable(pageRequest.getStartIndex(), pageRequest.getCount());
    return buildTokensListResponse(tokenRepository.findAllValidRefreshTokens(new Date(), op), op);
  }

  @Override
  public TokensListResponse<RefreshToken> getTokensForUser(String userId,
      TokensPageRequest pageRequest) {

    if (pageRequest.getCount() == 0) {
      return buildTokensCountResponse();
    }

    OffsetPageable op = new OffsetPageable(pageRequest.getStartIndex(), pageRequest.getCount());
    return buildTokensListResponse(
        tokenRepository.findValidRefreshTokensForUser(userId, new Date(), op), op);
  }

  @Override
  public TokensListResponse<RefreshToken> getTokensForClient(String clientId,
      TokensPageRequest pageRequest) {

    if (pageRequest.getCount() == 0) {
      return buildTokensCountResponse();
    }

    OffsetPageable op = new OffsetPageable(pageRequest.getStartIndex(), pageRequest.getCount());
    return buildTokensListResponse(
        tokenRepository.findValidRefreshTokensForClient(clientId, new Date(), op), op);
  }

  @Override
  public TokensListResponse<RefreshToken> getTokensForClientAndUser(String userId, String clientId,
      TokensPageRequest pageRequest) {

    if (pageRequest.getCount() == 0) {
      return buildTokensCountResponse();
    }

    OffsetPageable op = new OffsetPageable(pageRequest.getStartIndex(), pageRequest.getCount());
    return buildTokensListResponse(
        tokenRepository.findValidRefreshTokensForUserAndClient(userId, clientId, new Date(), op),
        op);
  }
}
