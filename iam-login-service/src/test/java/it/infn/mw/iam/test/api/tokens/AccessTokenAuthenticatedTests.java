package it.infn.mw.iam.test.api.tokens;

import static it.infn.mw.iam.api.tokens.TokensControllerSupport.CONTENT_TYPE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import it.infn.mw.iam.IamLoginService;
import it.infn.mw.iam.test.core.CoreControllerTestSupport;
import it.infn.mw.iam.test.util.WithMockOAuthUser;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {IamLoginService.class, CoreControllerTestSupport.class})
@WebAppConfiguration
public class AccessTokenAuthenticatedTests extends TokensUtils {

  private static final String TESTUSER_USERNAME = "test_102";
  private static final int FAKE_TOKEN_ID = 12345;

  @Before
  public void setup() {
    initMvc();
  }

  @Test
  @WithMockOAuthUser(user = TESTUSER_USERNAME, authorities = {"ROLE_USER"})
  public void getAccessTokenList() throws Exception {

    mvc.perform(get(ACCESS_TOKENS_BASE_PATH).contentType(CONTENT_TYPE)).andExpect(status().isForbidden());
  }

  @Test
  @WithMockOAuthUser(user = TESTUSER_USERNAME, authorities = {"ROLE_USER"})
  public void revokeAccessToken() throws Exception {

    String path = String.format("%s/%d", ACCESS_TOKENS_BASE_PATH, FAKE_TOKEN_ID);
    mvc.perform(delete(path).contentType(CONTENT_TYPE)).andExpect(status().isForbidden());
  }
}
