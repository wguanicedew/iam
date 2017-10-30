package it.infn.mw.iam.api.tokens.service;

import it.infn.mw.iam.api.tokens.model.TokensListResponse;
import it.infn.mw.iam.api.tokens.service.paging.TokensPageRequest;

public interface TokenService<T> {

  TokensListResponse<T> getAllTokens(final TokensPageRequest pageRequest);

  TokensListResponse<T> getTokensForUser(final String userId, final TokensPageRequest pageRequest);

  TokensListResponse<T> getTokensForClient(final String clientId,
      final TokensPageRequest pageRequest);

  TokensListResponse<T> getTokensForClientAndUser(final String userId, final String clientId,
      final TokensPageRequest pageRequest);

  T getTokenById(Long id);

  void revokeTokenById(Long id);
  
  void deleteAllTokens();

}
