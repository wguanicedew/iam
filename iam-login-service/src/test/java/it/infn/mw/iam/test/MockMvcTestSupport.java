package it.infn.mw.iam.test;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import it.infn.mw.iam.test.util.oauth.MockOAuth2Filter;

public class MockMvcTestSupport {

  @Autowired
  private WebApplicationContext context;

  protected MockMvc mvc;

  @Autowired
  protected MockOAuth2Filter mockOAuth2Filter;
  
  public void initMockMvc() {
    mvc =
        MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).alwaysDo(log()).build();
  }
  
  public MockMvc getMvc() {
    return mvc;
  }

  public MockOAuth2Filter getMockOAuth2Filter() {
    return mockOAuth2Filter;
  }

}
