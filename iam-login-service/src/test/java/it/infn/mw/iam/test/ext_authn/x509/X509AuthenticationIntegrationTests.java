package it.infn.mw.iam.test.ext_authn.x509;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.transaction.Transactional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import it.infn.mw.iam.IamLoginService;
import it.infn.mw.iam.persistence.repository.IamAccountRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {IamLoginService.class})
@WebAppConfiguration
@Transactional
public class X509AuthenticationIntegrationTests extends X509TestSupport {

  @Autowired
  private IamAccountRepository iamAccountRepo;
  
  @Autowired
  private WebApplicationContext context;

  private MockMvc mvc;
  
  @Before
  public void setup() {
    mvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).
        alwaysDo(print()).
        build();
  }
  
  
  @Test
  public void testX509AuthenticationSuccessUserNotFoundFailure() throws Exception {
      mvc.perform(MockMvcRequestBuilders.get("/")
            .headers(test0SSLHeadersVerificationSuccess()))
          .andExpect(status().isFound())
          .andExpect(redirectedUrl("http://localhost/login"));
  }
  
  

}
