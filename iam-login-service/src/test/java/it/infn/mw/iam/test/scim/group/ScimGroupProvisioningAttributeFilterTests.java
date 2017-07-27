package it.infn.mw.iam.test.scim.group;

import static it.infn.mw.iam.api.scim.model.ScimConstants.SCIM_CONTENT_TYPE;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.transaction.Transactional;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import it.infn.mw.iam.IamLoginService;
import it.infn.mw.iam.api.scim.model.ScimListResponse;
import it.infn.mw.iam.test.core.CoreControllerTestSupport;
import it.infn.mw.iam.test.scim.ScimUtils;
import it.infn.mw.iam.test.util.WithMockOAuthUser;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {IamLoginService.class, CoreControllerTestSupport.class})
@WebAppConfiguration
@Transactional
@WithMockOAuthUser(clientId = "scim-client-rw", scopes = {"scim:read"})
public class ScimGroupProvisioningAttributeFilterTests {

  @Autowired
  private WebApplicationContext context;

  private MockMvc mvc;

  private final static String GROUPS_URI = ScimUtils.getGroupsLocation();

  @Before
  public void setup() {
    mvc = MockMvcBuilders.webAppContextSetup(context)
      .apply(springSecurity())
      .alwaysDo(print())
      .build();
  }

  @Test
  public void testReuturnOnlyDisplayNameRequest() throws Exception {
    //@formatter:off
    mvc.perform(get(GROUPS_URI)
        .contentType(SCIM_CONTENT_TYPE)
        .param("count", "1")
        .param("attributes", "displayName"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.totalResults", equalTo(22)))
      .andExpect(jsonPath("$.itemsPerPage", equalTo(1)))
      .andExpect(jsonPath("$.startIndex", equalTo(1)))
      .andExpect(jsonPath("$.schemas", contains(ScimListResponse.SCHEMA)))
      .andExpect(jsonPath("$.Resources", hasSize(equalTo(1))))
      .andExpect(jsonPath("$.Resources[0].id", is(Matchers.not(nullValue()))))
      .andExpect(jsonPath("$.Resources[0].schemas", is(Matchers.not(nullValue()))))
      .andExpect(jsonPath("$.Resources[0].displayName", is(Matchers.not(nullValue()))));
    //@formatter:on
  }

  @Test
  public void testMultipleAttrsRequest() throws Exception {
    //@formatter:off
    mvc.perform(get(GROUPS_URI)
        .contentType(SCIM_CONTENT_TYPE)
        .param("count", "2")
        .param("attributes", "displayName"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.totalResults", equalTo(22)))
      .andExpect(jsonPath("$.itemsPerPage", equalTo(2)))
      .andExpect(jsonPath("$.startIndex", equalTo(1)))
      .andExpect(jsonPath("$.schemas", contains(ScimListResponse.SCHEMA)))
      .andExpect(jsonPath("$.Resources", hasSize(equalTo(2))))
      .andExpect(jsonPath("$.Resources[0].id", is(Matchers.not(nullValue()))))
      .andExpect(jsonPath("$.Resources[0].schemas", is(not(nullValue()))))
      .andExpect(jsonPath("$.Resources[0].displayName", is(not(nullValue()))));
    //@formatter:on
  }

}
