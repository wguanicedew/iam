package it.infn.mw.iam.test.api.requests;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

import com.fasterxml.jackson.databind.ObjectMapper;

import it.infn.mw.iam.IamLoginService;
import it.infn.mw.iam.api.requests.model.GroupRequestDto;
import it.infn.mw.iam.test.util.WithAnonymousUser;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {IamLoginService.class})
@WebAppConfiguration
@Transactional
public class GroupRequestsCreateTests extends GroupRequestsTestUtils {

  private final static String GROUP_REQUESTS_API_PATH = "/iam/group_requests";

  @Autowired
  private WebApplicationContext context;

  @Autowired
  private ObjectMapper mapper;

  private MockMvc mvc;
  private GroupRequestDto request;

  @Before
  public void setup() {
    mvc = MockMvcBuilders.webAppContextSetup(context)
      .apply(springSecurity())
      .alwaysDo(print())
      .build();

    request = buildGroupRequest(TEST_USERNAME, TEST_GROUPNAME);
  }

  @Test
  @WithMockUser(roles = {"ADMIN"})
  public void createGroupRequestAsAdmin() throws Exception {
    // @formatter:off
    mvc.perform(post(GROUP_REQUESTS_API_PATH)
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(request)))
      .andExpect(status().isOk());
    // @formatter:on
  }

  @Test
  @WithMockUser(roles = {"USER"}, username = TEST_USERNAME)
  public void createGroupRequestAsUser() throws Exception {
    // @formatter:off
    mvc.perform(post(GROUP_REQUESTS_API_PATH)
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(request)))
      .andExpect(status().isOk());
    // @formatter:on
  }

  @Test
  @WithMockUser(roles = {"USER"}, username = TEST_USERNAME)
  public void createGroupRequestAsAnotherUser() throws Exception {
    GroupRequestDto request = buildGroupRequest("test_200", TEST_GROUPNAME);

    // @formatter:off
    mvc.perform(post(GROUP_REQUESTS_API_PATH)
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(request)))
      .andExpect(status().isForbidden());
    // @formatter:on
  }

  @Test
  @WithAnonymousUser
  public void createGroupRequestAsAnonymous() throws Exception {
    // @formatter:off
    mvc.perform(post(GROUP_REQUESTS_API_PATH)
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(request)))
      .andExpect(status().isUnauthorized());
    // @formatter:on
  }

  @Test
  @WithMockUser(roles = {"USER"}, username = TEST_USERNAME)
  public void createGroupRequestWitInvalidNotes() throws Exception {

    request.setNotes(null);
    // @formatter:off
    mvc.perform(post(GROUP_REQUESTS_API_PATH)
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(request)))
      .andExpect(status().isBadRequest());
    // @formatter:on

    request.setNotes("");
    // @formatter:off
    mvc.perform(post(GROUP_REQUESTS_API_PATH)
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(request)))
      .andExpect(status().isBadRequest());
    // @formatter:on

    request.setNotes("   ");
    // @formatter:off
    mvc.perform(post(GROUP_REQUESTS_API_PATH)
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(request)))
      .andExpect(status().isBadRequest());
    // @formatter:on
  }

  @Test
  @WithMockUser(roles = {"USER"}, username = TEST_USERNAME)
  public void createGroupRequestWithInvalidGroup() throws Exception {

    request.setGroupName("");
    // @formatter:off
    mvc.perform(post(GROUP_REQUESTS_API_PATH)
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(request)))
      .andExpect(status().isBadRequest());
    // @formatter:on

    request.setGroupName("fake_group");
    // @formatter:off
    mvc.perform(post(GROUP_REQUESTS_API_PATH)
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(request)))
      .andExpect(status().isBadRequest());
    // @formatter:on
  }

  @Test
  @WithMockUser(roles = {"USER"}, username = TEST_USERNAME)
  public void createGroupRequestWithInvalidUsername() throws Exception {

    request.setUsername("");
    // @formatter:off
    mvc.perform(post(GROUP_REQUESTS_API_PATH)
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(request)))
      .andExpect(status().isBadRequest());
    // @formatter:on

    request.setUsername("fake_user");
    // @formatter:off
    mvc.perform(post(GROUP_REQUESTS_API_PATH)
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(request)))
      .andExpect(status().isBadRequest());
    // @formatter:on
  }

  @Test
  @WithMockUser(roles = {"USER"}, username = TEST_USERNAME)
  public void createGroupRequestAlreadyExists() throws Exception {
    savePendingGroupRequest(TEST_USERNAME, TEST_GROUPNAME);

    // @formatter:off
    mvc.perform(post(GROUP_REQUESTS_API_PATH)
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(request)))
      .andExpect(status().isBadRequest());
    // @formatter:on
  }

}
