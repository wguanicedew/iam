package it.infn.mw.iam.test.oauth;

import static com.google.common.collect.Sets.newHashSet;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mitre.util.JsonUtils.getAsArray;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Set;

import javax.transaction.Transactional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mitre.oauth2.model.ClientDetailsEntity;
import org.mitre.oauth2.model.RegisteredClientFields;
import org.mitre.openid.connect.ClientDetailsEntityJsonProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;
import com.google.common.collect.Sets;
import com.google.gson.JsonObject;

import it.infn.mw.iam.IamLoginService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = IamLoginService.class)
@WebAppConfiguration
@Transactional
public class ClientRegistrationScopeTests {

  private final static String REGISTER_ENDPOINT = "/register";

  @Autowired
  private WebApplicationContext context;

  @Autowired
  private ObjectMapper mapper;

  private MockMvc mvc;

  @Before
  public void setup() throws Exception {
    mvc = MockMvcBuilders.webAppContextSetup(context)
      .apply(springSecurity())
      .alwaysDo(print())
      .build();
  }

  @Test
  public void testClientRegistrationAccessTokenWorks() throws Exception {
    String clientName = "test_test_test";
    String jsonInString = buildClientJsonString(clientName, Sets.newHashSet("test"));

    // @formatter:off
    String response =
        mvc.perform(post(REGISTER_ENDPOINT)
            .contentType(APPLICATION_JSON)
            .content(jsonInString))
          .andExpect(status().isCreated())
          .andExpect(content().contentType(APPLICATION_JSON))
          .andReturn()
          .getResponse()
          .getContentAsString();
    // @formatter:on

    JsonNode jsonNode = mapper.readTree(response);

    String rat = jsonNode.get("registration_access_token").asText();
    String registrationUri = jsonNode.get("registration_client_uri").asText();

    assertThat(rat, notNullValue());
    assertThat(registrationUri, notNullValue());

    // @formatter:off
    mvc.perform(get(registrationUri)
        .contentType(APPLICATION_JSON)
        .header("Authorization", "Bearer " + rat))
      .andExpect(status().isOk())
      .andExpect(content().contentType(APPLICATION_JSON));
    
    mvc.perform(get(registrationUri)
        .contentType(APPLICATION_JSON)
        .header("Authorization", "Bearer " + rat))
      .andExpect(status().isOk())
      .andExpect(content().contentType(APPLICATION_JSON));
    // @formatter:on
  }

  @Test
  public void testCreateClientWithRegistrationReservedScopes() throws Exception {

    String clientName = "test_client";
    Set<String> scopes =
        Sets.newHashSet("registration:read", "registration:write", "scim:read", "scim:write");

    String jsonInString = buildClientJsonString(clientName, scopes);

    // @formatter:off
    String response =
        mvc.perform(post(REGISTER_ENDPOINT)
            .contentType(APPLICATION_JSON)
            .content(jsonInString))
          .andExpect(status().isCreated())
          .andExpect(content().contentType(APPLICATION_JSON))
          .andReturn()
          .getResponse()
          .getContentAsString();
    // @formatter:on

    ClientDetailsEntity saved = ClientDetailsEntityJsonProcessor.parse(response);

    assertNotNull(saved);
    for (String reservedScope : scopes) {
      assertThat(saved.getScope(), not(hasItem(reservedScope)));
    }
  }

  @Test
  public void testGetTokenWithScimReservedScopesFailure() throws Exception {

    String clientName = "test_client";
    Set<String> scopes =
        Sets.newHashSet("scim:read", "scim:write", "registration:read", "registration:write");

    String jsonInString = buildClientJsonString(clientName, scopes);

    // @formatter:off
    String response =
        mvc.perform(post(REGISTER_ENDPOINT)
            .contentType(APPLICATION_JSON)
            .content(jsonInString))
          .andExpect(status().isCreated())
          .andExpect(content().contentType(APPLICATION_JSON))
          .andReturn()
          .getResponse()
          .getContentAsString();
    // @formatter:on

    ClientDetailsEntity saved = ClientDetailsEntityJsonProcessor.parse(response);

    assertNotNull(saved);

    // @formatter:off
    mvc.perform(post("/token")
        .param("grant_type", "client_credentials")
        .param("client_id", saved.getClientId())
        .param("client_secret", saved.getClientSecret())
        .param("scope", setToString(scopes)))
      .andExpect(status().isBadRequest());
    // @formatter:on
  }


  private String buildClientJsonString(String clientName, Set<String> scopes) {

    JsonObject json = new JsonObject();
    json.addProperty(RegisteredClientFields.CLIENT_NAME, clientName);
    json.addProperty(RegisteredClientFields.SCOPE, setToString(scopes));
    json.add(RegisteredClientFields.REDIRECT_URIS, getAsArray(newHashSet("http://localhost:9090")));
    json.add(RegisteredClientFields.GRANT_TYPES, getAsArray(newHashSet("client_credentials")));
    json.add(RegisteredClientFields.RESPONSE_TYPES, getAsArray(newHashSet(), true));
    json.add(RegisteredClientFields.CONTACTS, getAsArray(newHashSet("test@iam.test")));
    json.add(RegisteredClientFields.CLAIMS_REDIRECT_URIS, getAsArray(newHashSet(), true));
    json.add(RegisteredClientFields.REQUEST_URIS, getAsArray(newHashSet(), true));

    return json.toString();
  }


  private String setToString(Set<String> scopes) {

    Joiner joiner = Joiner.on(RegisteredClientFields.SCOPE_SEPARATOR);
    return joiner.join(scopes);
  }

}
