package it.infn.mw.iam.api.tokens.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.mitre.oauth2.model.OAuth2RefreshTokenEntity;
import org.mitre.oauth2.service.impl.DefaultOAuth2ProviderTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import it.infn.mw.iam.api.tokens.converter.TokensConverter;
import it.infn.mw.iam.api.tokens.exception.TokenNotFoundException;
import it.infn.mw.iam.api.tokens.model.RefreshToken;
import it.infn.mw.iam.api.tokens.model.TokensListResponse;
import it.infn.mw.iam.api.tokens.service.paging.OffsetPageable;
import it.infn.mw.iam.api.tokens.service.paging.TokensPageRequest;
import it.infn.mw.iam.persistence.repository.IamOAuthRefreshTokenRepository;

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

  private Page<OAuth2RefreshTokenEntity> getAllValidTokens(OffsetPageable op) {

    return tokenRepository.findAllValidRefreshTokens(new Date(), op);
  }

  private int countAllValidTokens() {

    return tokenRepository.countValidRefreshTokens(new Date());
  }

  private Page<OAuth2RefreshTokenEntity> getAllValidTokensForUser(String userId,
      OffsetPageable op) {

    return tokenRepository.findValidRefreshTokensForUser(userId, new Date(), op);
  }

  private int countAllValidTokensForUser(String userId) {

    return tokenRepository.countValidRefreshTokensForUser(userId, new Date());
  }

  private Page<OAuth2RefreshTokenEntity> getAllValidTokensForClient(String clientId,
      OffsetPageable op) {

    return tokenRepository.findValidRefreshTokensForClient(clientId, new Date(), op);
  }

  private int countAllValidTokensForClient(String clientId) {

    return tokenRepository.countValidRefreshTokensForClient(clientId, new Date());
  }

  private Page<OAuth2RefreshTokenEntity> getAllValidTokensForUserAndClient(String userId,
      String clientId, OffsetPageable op) {

    return tokenRepository.findValidRefreshTokensForUserAndClient(userId, clientId, new Date(), op);
  }

  private int countAllValidTokensForUserAndClient(String userId, String clientId) {

    return tokenRepository.countValidRefreshTokensForUserAndClient(userId, clientId, new Date());
  }

  private TokensListResponse<RefreshToken> buildCountResponse(int countResponse) {

    return new TokensListResponse<>(Collections.emptyList(), countResponse, 0, 1);
  }

  private TokensListResponse<RefreshToken> buildListResponse(TokensPageRequest pageRequest,
      Page<OAuth2RefreshTokenEntity> entities) {

    List<RefreshToken> resources = new ArrayList<>();

    entities.getContent().forEach(a -> resources.add(tokensConverter.toRefreshToken(a)));

    return new TokensListResponse<>(resources, entities.getTotalElements(), resources.size(),
        pageRequest.getStartIndex() + 1);
  }

  private OffsetPageable getOffsetPageable(TokensPageRequest pageRequest) {

    if (pageRequest.getCount() == 0) {
      return new OffsetPageable(0, 1);
    }
    return new OffsetPageable(pageRequest.getStartIndex(), pageRequest.getCount());
  }

  private boolean isCountRequest(TokensPageRequest pageRequest) {

    return pageRequest.getCount() == 0;
  }

  @Override
  public TokensListResponse<RefreshToken> getAllTokens(TokensPageRequest pageRequest) {

    if (isCountRequest(pageRequest)) {

      int count = countAllValidTokens();
      return buildCountResponse(count);
    }

    OffsetPageable op = getOffsetPageable(pageRequest);
    Page<OAuth2RefreshTokenEntity> entities = getAllValidTokens(op);
    return buildListResponse(pageRequest, entities);
  }

  @Override
  public TokensListResponse<RefreshToken> getTokensForUser(String userId,
      TokensPageRequest pageRequest) {

    if (isCountRequest(pageRequest)) {

      int count = countAllValidTokensForUser(userId);
      return buildCountResponse(count);
    }

    OffsetPageable op = getOffsetPageable(pageRequest);
    Page<OAuth2RefreshTokenEntity> entities = getAllValidTokensForUser(userId, op);
    return buildListResponse(pageRequest, entities);
  }

  @Override
  public TokensListResponse<RefreshToken> getTokensForClient(String clientId,
      TokensPageRequest pageRequest) {

    if (isCountRequest(pageRequest)) {

      int count = countAllValidTokensForClient(clientId);
      return buildCountResponse(count);
    }

    OffsetPageable op = getOffsetPageable(pageRequest);
    Page<OAuth2RefreshTokenEntity> entities = getAllValidTokensForClient(clientId, op);
    return buildListResponse(pageRequest, entities);
  }

  @Override
  public TokensListResponse<RefreshToken> getTokensForClientAndUser(String userId, String clientId,
      TokensPageRequest pageRequest) {

    if (isCountRequest(pageRequest)) {

      int count = countAllValidTokensForUserAndClient(userId, clientId);
      return buildCountResponse(count);
    }

    OffsetPageable op = getOffsetPageable(pageRequest);
    Page<OAuth2RefreshTokenEntity> entities =
        getAllValidTokensForUserAndClient(userId, clientId, op);
    return buildListResponse(pageRequest, entities);
  }

  @Override
  public void deleteAllTokens() {

    tokenRepository.deleteAll();
  }
}
