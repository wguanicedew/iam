package it.infn.mw.iam.test.api.tokens;

import static it.infn.mw.iam.api.tokens.TokensControllerSupport.CONTENT_TYPE;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import it.infn.mw.iam.IamLoginService;
import it.infn.mw.iam.test.core.CoreControllerTestSupport;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {IamLoginService.class, CoreControllerTestSupport.class})
@WebAppConfiguration
public class AccessTokenAnonymousTests extends TestTokensUtils {

  private static final int FAKE_TOKEN_ID = 12345;

  @Before
  public void setup() {
    clearAllTokens();
    initMvc();
  }

  @Test
  public void authenticationRequiredOnGettingListTest() throws Exception {
    mvc.perform(get(ACCESS_TOKENS_BASE_PATH).contentType(CONTENT_TYPE)
        .with(authentication(anonymousAuthenticationToken()))).andExpect(unauthenticated());
  }

  @Test
  public void authenticationRequiredOnRevokingTest() throws Exception {

    String path = String.format("%s/%d", ACCESS_TOKENS_BASE_PATH, FAKE_TOKEN_ID);
    mvc.perform(
        delete(path).contentType(CONTENT_TYPE).with(authentication(anonymousAuthenticationToken())))
        .andExpect(unauthenticated());

  }
}
