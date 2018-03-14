package it.infn.mw.iam.test.api.requests;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

import it.infn.mw.iam.IamLoginService;
import it.infn.mw.iam.api.requests.model.GroupRequestDto;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {IamLoginService.class})
@WebAppConfiguration
@Transactional
public class GroupRequestsListTests extends GroupRequestsTestUtils {

  private final static String LIST_REQUESTS_URL = "/iam/group_requests/";

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

    // request = savePendingGroupRequest(TEST_USERNAME, TEST_GROUPNAME);
    for (int i = 1; i <= 20; i++) {
      savePendingGroupRequest(TEST_USERNAME, String.format("Test-%03d", i));
    }
  }

  @Test
  @WithMockUser(roles = {"ADMIN"})
  public void listGroupRequestAsAdmin() throws Exception {

    // @formatter:off
    mvc.perform(get(LIST_REQUESTS_URL)
        .contentType(MediaType.APPLICATION_JSON)
        .param("page", "0")
        .param("size", "10"))
      .andExpect(status().isOk());
    // @formatter:on
  }

}
