package it.infn.mw.iam.test.oauth.assertions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.nimbusds.jwt.JWT;

import it.infn.mw.iam.IamLoginService;
import it.infn.mw.iam.test.core.CoreControllerTestSupport;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {IamLoginService.class, CoreControllerTestSupport.class})
@WebAppConfiguration
@Transactional
public class JWTBearerClientAuthenticationTests extends JWTBearerClientAuthenticationTestSupport {


  @Before
  public void setup() throws Exception {
    buildMockMvc();
  }

  @Test
  public void testJwtAuth() throws Exception {
    JWT jwt = createClientAuthToken(CLIENT_ID_SECRET_JWT, Instant.now().plusSeconds(600));
    String serializedToken = jwt.serialize();

    mvc
      .perform(post(TOKEN_ENDPOINT).param("client_id", CLIENT_ID_SECRET_JWT)
        .param("client_assertion_type", JWT_BEARER_ASSERTION_TYPE)
        .param("client_assertion", serializedToken)
        .param("grant_type", "client_credentials"))
      .andExpect(status().isOk());


  }

}
