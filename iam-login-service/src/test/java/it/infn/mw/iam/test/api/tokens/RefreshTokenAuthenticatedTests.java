package it.infn.mw.iam.test.api.tokens;

import static it.infn.mw.iam.api.tokens.TokensControllerSupport.APPLICATION_JSON_CONTENT_TYPE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import it.infn.mw.iam.IamLoginService;
import it.infn.mw.iam.test.core.CoreControllerTestSupport;
import it.infn.mw.iam.test.util.WithMockOAuthUser;
import it.infn.mw.iam.test.util.oauth.MockOAuth2Filter;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {IamLoginService.class, CoreControllerTestSupport.class})
@WebAppConfiguration
public class RefreshTokenAuthenticatedTests extends TestTokensUtils {

  private static final String TESTUSER_USERNAME = "test_102";
  private static final int FAKE_TOKEN_ID = 12345;
  
  @Autowired
  private MockOAuth2Filter mockOAuth2Filter;

  @Before
  public void setup() {
    initMvc();
  }
  
  @After
  public void teardown() {
    mockOAuth2Filter.cleanupSecurityContext();
  }

  @Test
  @WithMockOAuthUser(user = TESTUSER_USERNAME, authorities = {"ROLE_USER"})
  public void forbiddenOnGettingListTest() throws Exception {

    mvc.perform(get(REFRESH_TOKENS_BASE_PATH).contentType(APPLICATION_JSON_CONTENT_TYPE)).andExpect(status().isForbidden());
  }

  @Test
  @WithMockOAuthUser(user = TESTUSER_USERNAME, authorities = {"ROLE_USER"})
  public void forbiddenOnRevokingTest() throws Exception {

    String path = String.format("%s/%d", REFRESH_TOKENS_BASE_PATH, FAKE_TOKEN_ID);
    mvc.perform(delete(path).contentType(APPLICATION_JSON_CONTENT_TYPE)).andExpect(status().isForbidden());
  }
}
