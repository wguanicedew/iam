package it.infn.mw.iam.test.api.requests;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
import it.infn.mw.iam.test.util.WithAnonymousUser;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {IamLoginService.class})
@WebAppConfiguration
@Transactional
public class GroupRequestsGetDetailsTests extends GroupRequestsTestUtils {

  private final static String GET_DETAILS_URL = "/iam/group_requests/{uuid}";

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
  public void getGroupRequestDetailsAsAdmin() throws Exception {
    // @formatter:off
    mvc.perform(get(GET_DETAILS_URL, request.getUuid()))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.uuid", equalTo(request.getUuid())))
      .andExpect(jsonPath("$.username", equalTo(request.getUsername())))
      .andExpect(jsonPath("$.groupName", equalTo(request.getGroupName())))
      .andExpect(jsonPath("$.status", equalTo(request.getStatus())))
      .andExpect(jsonPath("$.notes", equalTo(request.getNotes())));
    // @formatter:on
  }

  @Test
  @WithMockUser(roles = {"USER"}, username = TEST_USERNAME)
  public void getGroupRequestDetailsAsUser() throws Exception {
    // @formatter:off
    mvc.perform(get(GET_DETAILS_URL, request.getUuid()))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.uuid", equalTo(request.getUuid())))
      .andExpect(jsonPath("$.username", equalTo(request.getUsername())))
      .andExpect(jsonPath("$.groupName", equalTo(request.getGroupName())))
      .andExpect(jsonPath("$.status", equalTo(request.getStatus())));;
    // @formatter:on
  }

  @Test
  @WithMockUser(roles = {"USER"}, username = TEST_USERNAME)
  public void getGroupRequestDetailsOfAnotherUser() throws Exception {
    request = savePendingGroupRequest("test_101", TEST_GROUPNAME);
    // @formatter:off
    mvc.perform(get(GET_DETAILS_URL, request.getUuid()))
      .andExpect(status().isForbidden());
    // @formatter:on
  }

  @Test
  @WithAnonymousUser
  public void getGroupRequestDetailsAsAnonymous() throws Exception {
    // @formatter:off
    mvc.perform(get(GET_DETAILS_URL, request.getUuid()))
      .andExpect(status().isUnauthorized())
      .andExpect(jsonPath("$.error", containsString("unauthorized")))
      .andExpect(jsonPath("$.error_description", containsString("Full authentication is required")));
    // @formatter:on
  }

  @Test
  @WithMockUser(roles = {"ADMIN"})
  public void getDetailsOfNotExitingGroupRequest() throws Exception {

    String fakeRequestUuid = UUID.randomUUID().toString();
    // @formatter:off
    mvc.perform(get(GET_DETAILS_URL, fakeRequestUuid))
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.error", containsString("does not exist")));
    // @formatter:on
  }

  @Test
  @WithMockUser(roles = {"ADMIN", "USER"})
  public void getGroupRequestDetailsAsUserWithBothRoles() throws Exception {
    // @formatter:off
    mvc.perform(get(GET_DETAILS_URL, request.getUuid()))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.uuid", equalTo(request.getUuid())))
      .andExpect(jsonPath("$.username", equalTo(request.getUsername())))
      .andExpect(jsonPath("$.groupName", equalTo(request.getGroupName())))
      .andExpect(jsonPath("$.status", equalTo(request.getStatus())))
      .andExpect(jsonPath("$.notes", equalTo(request.getNotes())));
    // @formatter:on
  }

}
