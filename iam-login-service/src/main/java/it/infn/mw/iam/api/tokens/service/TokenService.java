package it.infn.mw.iam.api.tokens.service;

import it.infn.mw.iam.api.common.ListResponseDTO;
import it.infn.mw.iam.api.tokens.service.paging.TokensPageRequest;

public interface TokenService<T> {

  ListResponseDTO<T> getAllTokens(final TokensPageRequest pageRequest);

  ListResponseDTO<T> getTokensForUser(final String userId, final TokensPageRequest pageRequest);

  ListResponseDTO<T> getTokensForClient(final String clientId,
      final TokensPageRequest pageRequest);

  ListResponseDTO<T> getTokensForClientAndUser(final String userId, final String clientId,
      final TokensPageRequest pageRequest);

  T getTokenById(Long id);

  void revokeTokenById(Long id);
  
  void deleteAllTokens();

}
