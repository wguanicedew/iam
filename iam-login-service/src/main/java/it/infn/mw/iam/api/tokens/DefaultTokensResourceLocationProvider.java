package it.infn.mw.iam.api.tokens;

import static it.infn.mw.iam.api.tokens.Constants.ACCESS_TOKENS_ENDPOINT;
import static it.infn.mw.iam.api.tokens.Constants.REFRESH_TOKENS_ENDPOINT;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class DefaultTokensResourceLocationProvider implements TokensResourceLocationProvider {

  @Value("${iam.baseUrl}")
  private String baseUrl;

  @Override
  public String accessTokenLocation(Long accessTokenId) {
    return String.format("%s%s/%d", baseUrl, ACCESS_TOKENS_ENDPOINT, accessTokenId);
  }

  @Override
  public String refreshTokenLocation(Long refreshTokenId) {
    return String.format("%s%s/%d", baseUrl, REFRESH_TOKENS_ENDPOINT, refreshTokenId);
  }

}
