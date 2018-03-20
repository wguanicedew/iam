package it.infn.mw.iam.test.api.requests;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import it.infn.mw.iam.IamLoginService;
import it.infn.mw.iam.api.requests.model.GroupRequestDto;
import it.infn.mw.iam.core.IamGroupRequestStatus;
import it.infn.mw.iam.notification.NotificationStoreService;
import it.infn.mw.iam.test.util.WithAnonymousUser;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {IamLoginService.class})
@WebAppConfiguration
@Transactional
public class GroupRequestsApproveTests extends GroupRequestsTestUtils {

  private final static String APPROVE_URL = "/iam/group_requests/{uuid}/approve";

  @Autowired
  private WebApplicationContext context;

  @Autowired
  private NotificationStoreService notificationService;

  private MockMvc mvc;
  private GroupRequestDto request;

  @Before
  public void setup() {
    mvc = MockMvcBuilders.webAppContextSetup(context)
      .apply(springSecurity())
      .alwaysDo(print())
      .build();

    request = savePendingGroupRequest(TEST_USERNAME, TEST_GROUPNAME);
  }

  @Test
  @WithMockUser(roles = {"ADMIN"})
  public void approveGroupRequestAsAdmin() throws Exception {
    // @formatter:off
    String response = mvc.perform(post(APPROVE_URL, request.getUuid()))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.status", equalTo(IamGroupRequestStatus.APPROVED.name())))
      .andExpect(jsonPath("$.username", equalTo(TEST_USERNAME)))
      .andExpect(jsonPath("$.groupName", equalTo(TEST_GROUPNAME)))
      .andExpect(jsonPath("$.uuid", equalTo(request.getUuid())))
      .andExpect(jsonPath("$.lastUpdateTime").exists())
      .andExpect(jsonPath("$.lastUpdateTime").isNotEmpty())
      .andReturn()
      .getResponse()
      .getContentAsString();
    // @formatter:on

    GroupRequestDto result = mapper.readValue(response, GroupRequestDto.class);
    assertThat(result.getLastUpdateTime(), greaterThan(result.getCreationTime()));

    int mailCount = notificationService.countPendingNotifications();
    assertThat(mailCount, equalTo(1));
  }

  @Test
  @WithMockUser(roles = {"USER"}, username = TEST_USERNAME)
  public void approveGroupRequestAsUser() throws Exception {
    // @formatter:off
    mvc.perform(post(APPROVE_URL, request.getUuid()))
      .andExpect(status().isForbidden());
    // @formatter:on
  }

  @Test
  @WithAnonymousUser
  public void approveGroupRequestAsAnonymous() throws Exception {
    // @formatter:off
    mvc.perform(post(APPROVE_URL, request.getUuid()))
      .andExpect(status().isUnauthorized())
      .andExpect(jsonPath("$.error", containsString("unauthorized")))
      .andExpect(jsonPath("$.error_description", containsString("Full authentication is required")));
    // @formatter:on
  }

  @Test
  @WithMockUser(roles = {"ADMIN"})
  public void approveNotExitingGroupRequest() throws Exception {
    String fakeRequestUuid = UUID.randomUUID().toString();
    // @formatter:off
    mvc.perform(post(APPROVE_URL, fakeRequestUuid))
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.error", containsString("does not exist")));
    // @formatter:on
  }

  @Test
  @WithMockUser(roles = {"ADMIN"})
  public void approveAlreadyApprovedRequest() throws Exception {
    request = saveApprovedGroupRequest(TEST_USERNAME, TEST_GROUPNAME);
    // @formatter:off
    mvc.perform(post(APPROVE_URL, request.getUuid()))
    .andExpect(status().isBadRequest())
    .andExpect(jsonPath("$.error", containsString("Group request wrong transition")));
    // @formatter:on
  }

  @Test
  @WithMockUser(roles = {"ADMIN"})
  public void approveRejectedRequest() throws Exception {
    request = saveRejectedGroupRequest(TEST_USERNAME, TEST_GROUPNAME);
    // @formatter:off
    mvc.perform(post(APPROVE_URL, request.getUuid()))
    .andExpect(status().isBadRequest())
    .andExpect(jsonPath("$.error", containsString("Group request wrong transition")));
    // @formatter:on
  }

}
