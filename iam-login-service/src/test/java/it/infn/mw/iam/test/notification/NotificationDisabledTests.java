package it.infn.mw.iam.test.notification;

import static it.infn.mw.iam.test.util.MockSmtpServerUtils.startMockSmtpServer;
import static it.infn.mw.iam.test.util.MockSmtpServerUtils.stopMockSmtpServer;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.UnsupportedEncodingException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.subethamail.wiser.Wiser;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.infn.mw.iam.IamLoginService;
import it.infn.mw.iam.notification.NotificationService;
import it.infn.mw.iam.persistence.model.IamAccount;
import it.infn.mw.iam.persistence.repository.IamAccountRepository;
import it.infn.mw.iam.persistence.repository.IamEmailNotificationRepository;
import it.infn.mw.iam.registration.RegistrationRequestDto;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = IamLoginService.class)
@WebAppConfiguration
@TestPropertySource(properties = {"notification.disable=true"})
public class NotificationDisabledTests {

  public static final String REGISTRATION_CREATE_ENDPOINT = "/registration/create";

  @Autowired
  @Qualifier("defaultNotificationService")
  private NotificationService notificationService;

  @Autowired
  private IamEmailNotificationRepository notificationRepository;

  @Value("${spring.mail.host}")
  private String mailHost;

  @Value("${spring.mail.port}")
  private Integer mailPort;

  private Wiser wiser;

  @Autowired
  ObjectMapper mapper;

  @Autowired
  private WebApplicationContext context;

  @Autowired
  private IamAccountRepository accountRepository;

  private MockMvc mvc;

  @Before
  public void setUp() throws InterruptedException {
    wiser = startMockSmtpServer(mailHost, mailPort);
    mvc = MockMvcBuilders.webAppContextSetup(context)
      .apply(springSecurity())
      .alwaysDo(print())
      .build();
  }

  @After
  public void tearDown() throws InterruptedException {
    stopMockSmtpServer(wiser);
    notificationRepository.deleteAll();
    if (wiser.getServer().isRunning()) {
      Assert.fail("Fake mail server is still running after stop!!");
    }
  }



  @Test
  public void testDisableNotificationOption() throws UnsupportedEncodingException, Exception {
    RegistrationRequestDto req = new RegistrationRequestDto();

    req.setGivenname("testDisableNotificationOption");
    req.setFamilyname("User");
    req.setEmail("testDisableNotificationOption@example.org");
    req.setUsername("testDisableNotificationOption");
    req.setNotes("testDisableNotificationOption");

    String jsonReq = mapper.writeValueAsString(req);

    String response = mvc
      .perform(post(REGISTRATION_CREATE_ENDPOINT).content(jsonReq)
        .contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andReturn()
      .getResponse()
      .getContentAsString();

    RegistrationRequestDto savedRequest = mapper.readValue(response, RegistrationRequestDto.class);

    notificationService.sendPendingNotifications();

    assertThat(wiser.getMessages(), hasSize(0));

//    Iterable<IamEmailNotification> queue = notificationRepository.findAll();
//
//    for (IamEmailNotification elem : queue) {
//      assertThat(elem.getDeliveryStatus(), equalTo(IamDeliveryStatus.PENDING));
//    }

    IamAccount account = accountRepository.findByUuid(savedRequest.getAccountId())
      .orElseThrow(() -> new AssertionError("Expected account not found!"));
    
    accountRepository.delete(account);

  }

}
