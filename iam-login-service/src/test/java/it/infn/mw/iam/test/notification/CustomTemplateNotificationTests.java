/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2016-2019
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package it.infn.mw.iam.test.notification;

import com.fasterxml.jackson.databind.ObjectMapper;
import freemarker.template.Configuration;
import it.infn.mw.iam.IamLoginService;
import it.infn.mw.iam.notification.NotificationProperties;
import it.infn.mw.iam.persistence.model.IamEmailNotification;
import it.infn.mw.iam.registration.PersistentUUIDTokenGenerator;
import it.infn.mw.iam.registration.RegistrationRequestDto;
import it.infn.mw.iam.test.core.CoreControllerTestSupport;
import it.infn.mw.iam.test.util.WithAnonymousUser;
import it.infn.mw.iam.test.util.notification.MockNotificationDelivery;
import it.infn.mw.iam.test.util.oauth.MockOAuth2Filter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {IamLoginService.class, NotificationTestConfig.class,
    CoreControllerTestSupport.class})
@WebAppConfiguration
@Transactional
@WithAnonymousUser
@TestPropertySource(properties = {
        "notification.disable=false"
})
public class CustomTemplateNotificationTests {

  @Autowired
  private NotificationProperties properties;

  @Value("${spring.mail.host}")
  private String mailHost;

  @Value("${spring.mail.port}")
  private Integer mailPort;

  @Value("${iam.organisation.name}")
  private String organisationName;

  @Value("${iam.baseUrl}")
  private String baseUrl;

  //@Value("${notification.customTemplateLocation}")
  //private String customTemplateLocation = System.getProperty("user.dir");

  @Autowired
  private PersistentUUIDTokenGenerator generator;

  @Autowired
  private MockNotificationDelivery notificationDelivery;

  @Autowired
  private MockOAuth2Filter mockOAuth2Filter;

  @Autowired
  private WebApplicationContext context;

  @Autowired
  private ObjectMapper mapper;

  @Autowired
  private Configuration fmConfig;

  private MockMvc mvc;

  @Before
  public void setUp() throws InterruptedException {
    mvc =
        MockMvcBuilders.webAppContextSetup(context).alwaysDo(log()).apply(springSecurity()).build();
    fmConfig.clearTemplateCache();
  }

  @After
  public void tearDown() throws InterruptedException {
    mockOAuth2Filter.cleanupSecurityContext();
    notificationDelivery.clearDeliveredNotifications();
    fmConfig.clearTemplateCache();
  }

  @Test
  public void testCustomTemplateNewReg() throws Exception {
    String username = "test_custom_template";

    RegistrationRequestDto request = new RegistrationRequestDto();
    request.setGivenname("Test");
    request.setFamilyname("Test");
    request.setEmail("test_custom_template@example.com");
    request.setUsername(username);
    request.setNotes("My notes go here...");

    String responseJson = mvc
      .perform(post("/registration/create").contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(request)))
      .andExpect(MockMvcResultMatchers.status().isOk())
      .andReturn()
      .getResponse()
      .getContentAsString();

    request = mapper.readValue(responseJson, RegistrationRequestDto.class);

    notificationDelivery.sendPendingNotifications();

    assertThat(notificationDelivery.getDeliveredNotifications(), hasSize(1));

    IamEmailNotification message = notificationDelivery.getDeliveredNotifications().get(0);

    assertThat(message.getSubject(), equalTo(properties.getSubject().get("confirmation")));

    assertThat(message.getBody(),
            containsString("you have requested to be a member of"));
    //assertThat(message.getBody(),
    //        containsString("ON DISK"));

    notificationDelivery.clearDeliveredNotifications();
  }

}
