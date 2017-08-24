package it.infn.mw.iam.test.api.tokens;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import it.infn.mw.iam.api.tokens.Constants;
import it.infn.mw.iam.core.user.exception.IamAccountException;
import it.infn.mw.iam.persistence.model.IamAccount;
import it.infn.mw.iam.persistence.repository.IamAccountRepository;
import it.infn.mw.iam.persistence.repository.IamOAuthAccessTokenRepository;
import it.infn.mw.iam.persistence.repository.IamOAuthRefreshTokenRepository;
import it.infn.mw.iam.test.util.oauth.MockOAuth2Request;

import org.mitre.oauth2.model.ClientDetailsEntity;
import org.mitre.oauth2.model.OAuth2AccessTokenEntity;
import org.mitre.oauth2.service.ClientDetailsEntityService;
import org.mitre.oauth2.service.impl.DefaultOAuth2ProviderTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

public class TestTokensUtils {

  protected static final String REFRESH_TOKENS_BASE_PATH = Constants.REFRESH_TOKENS_ENDPOINT;
  protected static final String ACCESS_TOKENS_BASE_PATH = Constants.ACCESS_TOKENS_ENDPOINT;

  @Autowired
  private IamOAuthAccessTokenRepository accessTokenRepository;

  @Autowired
  private IamOAuthRefreshTokenRepository refreshTokenRepository;

  @Autowired
  private ClientDetailsEntityService clientDetailsService;

  @Autowired
  private IamAccountRepository accountRepository;

  @Autowired
  protected DefaultOAuth2ProviderTokenService tokenService;

  @Autowired
  private WebApplicationContext context;

  protected MockMvc mvc;

  public void initMvc() {
    initMvc(context);
  }

  public void initMvc(WebApplicationContext context) {
    mvc = MockMvcBuilders.webAppContextSetup(context)
        .apply(springSecurity())
        .alwaysDo(print())
        .build();
  }

  private OAuth2Authentication oauth2Authentication(ClientDetailsEntity client, String username,
      String[] scopes) {

    Authentication userAuth = null;

    if (username != null) {
      userAuth = new UsernamePasswordAuthenticationToken(username, "");
    }

    MockOAuth2Request req = new MockOAuth2Request(client.getClientId(), scopes);
    OAuth2Authentication auth = new OAuth2Authentication(req, userAuth);

    return auth;
  }

  public ClientDetailsEntity loadTestClient(String clientId) {
    return clientDetailsService.loadClientByClientId(clientId);
  }

  public IamAccount loadTestUser(String userId) {
    return accountRepository.findByUsername(userId)
        .orElseThrow(() -> new IamAccountException("User not found"));
  }

  public OAuth2AccessTokenEntity buildAccessToken(ClientDetailsEntity client, String username,
      String[] scopes) {
    OAuth2AccessTokenEntity token =
        tokenService.createAccessToken(oauth2Authentication(client, username, scopes));
    return token;
  }

  public OAuth2AccessTokenEntity buildAccessToken(ClientDetailsEntity client, String[] scopes) {
    OAuth2AccessTokenEntity token =
        tokenService.createAccessToken(oauth2Authentication(client, null, scopes));
    return token;
  }

  public void clearAllTokens() {
    accessTokenRepository.deleteAll();
    refreshTokenRepository.deleteAll();
  }

  public Authentication anonymousAuthenticationToken() {
    return new AnonymousAuthenticationToken("key", "anonymous",
        AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS"));
  }
}
