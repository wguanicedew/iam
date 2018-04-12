package it.infn.mw.iam.test.scim.me;

import static it.infn.mw.iam.api.scim.model.ScimConstants.SCIM_CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Date;

import org.springframework.transaction.annotation.Transactional;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.infn.mw.iam.IamLoginService;
import it.infn.mw.iam.api.aup.model.AupConverter;
import it.infn.mw.iam.api.aup.model.AupDTO;
import it.infn.mw.iam.api.scim.model.ScimIndigoUser;
import it.infn.mw.iam.test.api.aup.AupTestSupport;
import it.infn.mw.iam.test.core.CoreControllerTestSupport;
import it.infn.mw.iam.test.util.MockTimeProvider;
import it.infn.mw.iam.test.util.oauth.MockOAuth2Filter;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {IamLoginService.class, CoreControllerTestSupport.class})
@WebAppConfiguration
@Transactional
public class ScimMeAupTests extends AupTestSupport {

  private final static String ME_ENDPOINT = "/scim/Me";

  @Autowired
  private WebApplicationContext context;

  @Autowired
  private MockOAuth2Filter mockOAuth2Filter;
  
  @Autowired
  private AupConverter aupConverter;
  
  @Autowired
  private MockTimeProvider mockTimeProvider;
  
  @Autowired
  private ObjectMapper mapper;
  
  private MockMvc mvc;



  @Before
  public void setup() throws Exception {
    mockOAuth2Filter.cleanupSecurityContext();
    mvc = MockMvcBuilders.webAppContextSetup(context)
      .apply(springSecurity())
      .alwaysDo(print())
      .build();
  }

  @After
  public void teardown() {
    mockOAuth2Filter.cleanupSecurityContext();
  }

  @Test
  @WithMockUser(username = "admin", roles = {"ADMIN", "USER"})
  public void meEndpointAupSignatureTests() throws Exception {
    mvc.perform(get(ME_ENDPOINT).contentType(SCIM_CONTENT_TYPE))
      .andExpect(status().isOk())
      .andExpect(
          jsonPath("$." + ScimIndigoUser.INDIGO_USER_SCHEMA.AUP_SIGNATURE_TIME).doesNotExist());

    AupDTO aup = aupConverter.dtoFromEntity(buildDefaultAup());
    
    Date now = new Date();
    mockTimeProvider.setTime(now.getTime());

    mvc
      .perform(
          post("/iam/aup").contentType(APPLICATION_JSON).content(mapper.writeValueAsString(aup)))
      .andExpect(status().isCreated());
    
    mvc.perform(post("/iam/aup/signature")).andExpect(status().isCreated());
    mvc.perform(get(ME_ENDPOINT).contentType(SCIM_CONTENT_TYPE))
    .andExpect(status().isOk())
    .andExpect(
        jsonPath("$." + ScimIndigoUser.INDIGO_USER_SCHEMA.AUP_SIGNATURE_TIME).exists());
    
  }

}
