package it.infn.mw.iam.test.login;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.transaction.Transactional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import it.infn.mw.iam.IamLoginService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {IamLoginService.class})
@WebAppConfiguration
@Transactional
public class LoginTests {

  public static final String LOGIN_URL = "/login";
  public static final String ADMIN_USERNAME = "admin";
  public static final String ADMIN_PASSWORD = "password";
  
  @Autowired
  private WebApplicationContext context;
  
  
  private MockMvc mvc;
  
  @Before
  public void setup() {
    mvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
  }
  
  
  
  @Test
  public void loginForAdminUserWorks() throws Exception{
    MockHttpSession session =  (MockHttpSession) mvc.perform(
        post(LOGIN_URL)
        .param("username", ADMIN_USERNAME)
        .param("password", ADMIN_PASSWORD)
        .param("submit", "Login"))
        .andExpect(status().is3xxRedirection())
        .andExpect(MockMvcResultMatchers.redirectedUrl("/dashboard"))
        .andReturn()
        .getRequest()
        .getSession();
    
     MvcResult result = mvc.perform(get("/dashboard").session(session))
        .andExpect(status().isOk())
        .andExpect(MockMvcResultMatchers.view().name("iam/dashboard"))
        .andReturn();
  }
  
  @Test
  public void loginWithInvalidCredentialsIsBlocked() throws Exception{
    mvc.perform(
        post(LOGIN_URL)
        .param("username", ADMIN_USERNAME)
        .param("password", "whatever")
        .param("submit", "Login"))
        .andExpect(status().is3xxRedirection())
        .andExpect(MockMvcResultMatchers.redirectedUrl("/login?error=failure"));
    
    mvc.perform(
        post(LOGIN_URL)
        .param("username", "whatever")
        .param("password", "whatever")
        .param("submit", "Login"))
        .andExpect(status().is3xxRedirection())
        .andExpect(MockMvcResultMatchers.redirectedUrl("/login?error=failure"));
    
  }
}
