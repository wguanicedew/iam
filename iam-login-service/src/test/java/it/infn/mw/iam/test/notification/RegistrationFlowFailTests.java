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

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.util.NestedServletException;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.infn.mw.iam.IamLoginService;
import it.infn.mw.iam.registration.RegistrationRequestDto;
import it.infn.mw.iam.test.core.CoreControllerTestSupport;
import it.infn.mw.iam.test.util.WithAnonymousUser;
import it.infn.mw.iam.test.util.notification.MockNotificationDelivery;
import it.infn.mw.iam.test.util.oauth.MockOAuth2Filter;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {IamLoginService.class, NotificationTestConfig.class,
    CoreControllerTestSupport.class})
@WebAppConfiguration
@Transactional
@WithAnonymousUser
@TestPropertySource(
    properties = {"notification.disable=false", "spring.freemarker.template-loader-path=/invalid/"})
public class RegistrationFlowFailTests {

  @Value("${spring.mail.host}")
  private String mailHost;

  @Value("${spring.mail.port}")
  private Integer mailPort;

  @Value("${iam.organisation.name}")
  private String organisationName;

  @Value("${iam.baseUrl}")
  private String baseUrl;

  @Autowired
  private MockNotificationDelivery notificationDelivery;

  @Autowired
  private MockOAuth2Filter mockOAuth2Filter;

  @Autowired
  private WebApplicationContext context;

  @Autowired
  private ObjectMapper mapper;

  private MockMvc mvc;

  @Before
  public void setUp() throws InterruptedException {
    mvc =
        MockMvcBuilders.webAppContextSetup(context).alwaysDo(log()).apply(springSecurity()).build();
  }

  @After
  public void tearDown() throws InterruptedException {
    mockOAuth2Filter.cleanupSecurityContext();
    notificationDelivery.clearDeliveredNotifications();
  }

  @Test
  public void testSendWithEmptyQueue() {
    notificationDelivery.sendPendingNotifications();
    assertThat(notificationDelivery.getDeliveredNotifications(), hasSize(0));
  }

  @Test(expected = NestedServletException.class)
  public void testBadTemplateDir() throws Exception {
    String username = "baddir_flow";

    RegistrationRequestDto request = new RegistrationRequestDto();
    request.setGivenname("Badddir flow");
    request.setFamilyname("Test");
    request.setEmail("Baddir@example.com");
    request.setUsername(username);
    request.setNotes("Some short notes...");

    mvc
      .perform(post("/registration/create").contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(request)))
      .andExpect(status().isInternalServerError());

  }
}
