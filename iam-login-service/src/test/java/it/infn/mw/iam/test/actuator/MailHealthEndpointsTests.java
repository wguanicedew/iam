package it.infn.mw.iam.test.actuator;

import static it.infn.mw.iam.test.TestUtils.waitIfPortIsUsed;
import static java.lang.String.format;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.subethamail.wiser.Wiser;

import it.infn.mw.iam.IamLoginService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {IamLoginService.class})
@WebAppConfiguration
public class MailHealthEndpointsTests {

  private static final String ADMIN_USERNAME = "admin";
  private static final String ADMIN_ROLE = "ADMIN";

  private static final String USER_USERNAME = "test";
  private static final String USER_ROLE = "USER";

  private static final String STATUS_UP = "UP";

  @Value("${spring.mail.host}")
  private String mailHost;

  @Value("${spring.mail.port}")
  private Integer mailPort;

  @Value("${endpoints.healthMail.path}")
  private String mailEndpointPath;

  @Autowired
  private WebApplicationContext context;

  private MockMvc mvc;
  private Wiser wiser;

  @Before
  public synchronized void setup() throws InterruptedException {

    mvc = MockMvcBuilders.webAppContextSetup(context)
      .apply(springSecurity())
      .alwaysDo(print())
      .build();

    waitIfPortIsUsed(mailHost, mailPort, 30);

    wiser = new Wiser();
    wiser.setHostname(mailHost);
    wiser.setPort(mailPort);
    wiser.start();
  }

  @After
  public synchronized void teardown() {

    wiser.stop();
    if (wiser.getServer().isRunning()) {
      Assert.fail("Fake mail server is still running after stop!!");
    }
  }

  @Test
  public void testMailHealthEndpointWithSmtp() throws Exception {

    // @formatter:off
    mvc.perform(get(mailEndpointPath))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.status", equalTo(STATUS_UP)))
      .andExpect(jsonPath("$.mail").doesNotExist());
    // @formatter:on
  }

  @Test
  @WithMockUser(username = USER_USERNAME, roles = {USER_ROLE})
  public void testMailHealthEndpointWithSmtpAsUser() throws Exception {

    // @formatter:off
    mvc.perform(get(mailEndpointPath))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.status", equalTo(STATUS_UP)))
      .andExpect(jsonPath("$.mail").doesNotExist());
      ;
    // @formatter:on
  }

  @Test
  @WithMockUser(username = ADMIN_USERNAME, roles = {ADMIN_ROLE})
  public void testMailHealthEndpointWithSmtpAsAdmin() throws Exception {

    // @formatter:off
    mvc.perform(get(mailEndpointPath))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.status", equalTo(STATUS_UP)))
      .andExpect(jsonPath("$.mail.status", equalTo(STATUS_UP)))
      .andExpect(jsonPath("$.mail.location", equalTo(format("%s:%d", mailHost, mailPort))))
      ;
    // @formatter:on
  }

}
