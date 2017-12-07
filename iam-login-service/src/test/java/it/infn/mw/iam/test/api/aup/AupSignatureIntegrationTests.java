package it.infn.mw.iam.test.api.aup;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.transaction.Transactional;

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
import it.infn.mw.iam.api.aup.model.AupSignatureConverter;
import it.infn.mw.iam.persistence.model.IamAup;
import it.infn.mw.iam.persistence.repository.IamAupRepository;
import it.infn.mw.iam.persistence.repository.IamAupSignatureRepository;
import it.infn.mw.iam.test.core.CoreControllerTestSupport;
import it.infn.mw.iam.test.util.WithAnonymousUser;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {IamLoginService.class, CoreControllerTestSupport.class})
@WebAppConfiguration
@Transactional
@WithAnonymousUser
public class AupSignatureIntegrationTests extends AupTestSupport {

  @Autowired
  private WebApplicationContext context;

  @Autowired
  private ObjectMapper mapper;

  @Autowired
  private IamAupRepository aupRepo;

  @Autowired
  private IamAupSignatureRepository signatureRepo;

  @Autowired
  private AupConverter aupConverter;

  @Autowired
  private AupSignatureConverter signatureConverter;

  private MockMvc mvc;

  @Before
  public void setup() {
    mvc = MockMvcBuilders.webAppContextSetup(context)
      .alwaysDo(print())
      .apply(springSecurity())
      .build();
  }

  @Test
  public void getAupSignatureRequiresAuthenticatedUser() throws Exception {
    mvc.perform(get("/iam/aup/signature")).andExpect(status().isUnauthorized());
  }

  @Test
  public void signAupSignatureRequiresAuthenticatedUser() throws Exception {
    mvc.perform(post("/iam/aup/signature")).andExpect(status().isUnauthorized());
  }

  @Test
  @WithMockUser(username = "test", roles = {"USER"})
  public void getAupSignatureWithDefaultAupReturns404() throws Exception {
    mvc.perform(get("/iam/aup/signature")).andExpect(status().isNotFound()).andExpect(
        jsonPath("$.error", equalTo("AUP signature not found for user 'test'")));
  }

  @Test
  @WithMockUser(username = "test", roles = {"USER"})
  public void getAupSignatureWithNoSignatureRecordReturns404() throws Exception {
    IamAup aup = buildDefaultAup();
    aupRepo.save(aup);
    mvc.perform(get("/iam/aup/signature")).andExpect(status().isNotFound()).andExpect(
        jsonPath("$.error", equalTo("AUP signature not found for user 'test'")));
  }

  @Test
  @WithMockUser(username = "test", roles = {"USER"})
  public void signatureCreationReturns204() throws Exception {
    IamAup aup = buildDefaultAup();
    aupRepo.save(aup);
    
    mvc.perform(post("/iam/aup/signature")).andExpect(status().isCreated());
    mvc.perform(post("/iam/aup/signature")).andExpect(status().isCreated());

    mvc.perform(get("/iam/aup/signature"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.aup").exists())
      .andExpect(jsonPath("$.account.uuid").exists())
      .andExpect(jsonPath("$.account.username", equalTo("test")))
      .andExpect(jsonPath("$.account.name", equalTo("Test User")))
      .andExpect(jsonPath("$.signatureTime").exists());
  }

}
