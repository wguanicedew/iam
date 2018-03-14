package it.infn.mw.iam.test.api.requests;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import it.infn.mw.iam.IamLoginService;
import it.infn.mw.iam.api.requests.model.GroupRequestDto;
import it.infn.mw.iam.test.util.WithAnonymousUser;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {IamLoginService.class})
@WebAppConfiguration
@Transactional
public class GroupRequestsRejectTests extends GroupRequestsTestUtils {

  private final static String REJECT_URL = "/iam/group_requests/{uuid}/reject";

  @Autowired
  private WebApplicationContext context;

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
  public void rejectGroupRequestAsAdmin() throws Exception {
    // @formatter:off
    mvc.perform(post(REJECT_URL, request.getUuid())
        .param("motivation", TEST_REJECT_MOTIVATION)
        .contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk());
    // @formatter:on
  }

  @Test
  @WithMockUser(roles = {"USER"}, username = TEST_USERNAME)
  public void rejectGroupRequestAsUser() throws Exception {
    // @formatter:off
    mvc.perform(post(REJECT_URL, request.getUuid())
        .param("motivation", TEST_REJECT_MOTIVATION)
        .contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isForbidden());
    // @formatter:on
  }

  @Test
  @WithAnonymousUser
  public void rejectGroupRequestAsAnonymous() throws Exception {
    // @formatter:off
    mvc.perform(post(REJECT_URL, request.getUuid())
        .param("motivation", TEST_REJECT_MOTIVATION)
        .contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isUnauthorized());
    // @formatter:on
  }

  @Test
  @WithMockUser(roles = {"ADMIN"})
  public void rejectNotExitingGroupRequest() throws Exception {

    String fakeRequestUuid = UUID.randomUUID().toString();

    // @formatter:off
    mvc.perform(post(REJECT_URL, fakeRequestUuid)
        .param("motivation", TEST_REJECT_MOTIVATION)
        .contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isBadRequest());
    // @formatter:on
  }

  @Test
  @WithMockUser(roles = {"ADMIN"})
  public void rejectAlreadyRejectedRequest() throws Exception {

    request = saveRejectedGroupRequest(TEST_USERNAME, TEST_GROUPNAME);

    // @formatter:off
    mvc.perform(post(REJECT_URL, request.getUuid())
        .param("motivation", TEST_REJECT_MOTIVATION)
        .contentType(MediaType.APPLICATION_JSON))
    .andExpect(status().isBadRequest());
    // @formatter:on
  }

  @Test
  @WithMockUser(roles = {"ADMIN"})
  public void rejectAlreadyApprovedRequest() throws Exception {

    request = saveApprovedGroupRequest(TEST_USERNAME, TEST_GROUPNAME);

    // @formatter:off
    mvc.perform(post(REJECT_URL, request.getUuid())
        .param("motivation", TEST_REJECT_MOTIVATION)
        .contentType(MediaType.APPLICATION_JSON))
    .andExpect(status().isBadRequest());
    // @formatter:on
  }

  @Test
  @WithMockUser(roles = {"ADMIN"})
  public void rejectRequestWithoutMotivation() throws Exception {
    // @formatter:off
    mvc.perform(post(REJECT_URL, request.getUuid())
        .contentType(MediaType.APPLICATION_JSON))
    .andExpect(status().isBadRequest());
    
    mvc.perform(post(REJECT_URL, request.getUuid())
        .param("motivation", "")
        .contentType(MediaType.APPLICATION_JSON))
    .andExpect(status().isBadRequest());
    
    mvc.perform(post(REJECT_URL, request.getUuid())
        .param("motivation", "     ")
        .contentType(MediaType.APPLICATION_JSON))
    .andExpect(status().isBadRequest());
    // @formatter:on
  }
}
