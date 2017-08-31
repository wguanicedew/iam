package it.infn.mw.iam.test.registration;

import static it.infn.mw.iam.core.IamRegistrationRequestStatus.APPROVED;
import static it.infn.mw.iam.core.IamRegistrationRequestStatus.CONFIRMED;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.transaction.Transactional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.infn.mw.iam.IamLoginService;
import it.infn.mw.iam.registration.PersistentUUIDTokenGenerator;
import it.infn.mw.iam.registration.RegistrationRequestDto;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {IamLoginService.class})
@WebAppConfiguration
@Transactional
public class RegistrationUnprivilegedTests {

  @Autowired
  private WebApplicationContext context;

  @Autowired
  private PersistentUUIDTokenGenerator generator;

  @Autowired
  private ObjectMapper objectMapper;

  private MockMvc mvc;

  @Before
  public void setup() {
    mvc = MockMvcBuilders.webAppContextSetup(context)
      .apply(springSecurity())
      .alwaysDo(print())
      .build();
  }

  @Test
  public void testCreateRequest() throws Exception {

    RegistrationRequestDto reg = createRegistrationRequest("test_create");

    assertNotNull(reg);
    assertThat(reg.getUsername(), equalTo("test_create"));
    assertThat(reg.getGivenname(), equalTo("Test"));
    assertThat(reg.getFamilyname(), equalTo("User"));
    assertThat(reg.getEmail(), equalTo("test_create@example.org"));
    assertThat(reg.getNotes(), equalTo("Some short notes..."));
  }

  @Test
  public void testConfirmRequest() throws Exception {

    createRegistrationRequest("test_confirm");
    String token = generator.getLastToken();
    assertNotNull(token);
    confirmRegistrationRequest(token);
  }

  @Test
  public void testListRequestsUnauthorized() throws Exception {

    // @formatter:off
    mvc.perform(get("/registration/list")
        .with(authentication(anonymousAuthenticationToken())))
      .andExpect(status().isUnauthorized());
    // @formatter:on
  }

  @Test
  public void testConfirmRequestFailureWithWrongToken() throws Exception {

    createRegistrationRequest("test_confirm_fail");
    String badToken = "abcdefghilmnopqrstuvz";

    // @formatter:off
    mvc.perform(get("/registration/confirm/{token}", badToken))
      .andExpect(status().isNotFound());
    // @formatter:on
  }

  @Test
  public void testApproveRequestUnauthorized() throws Exception {

    RegistrationRequestDto reg = createRegistrationRequest("test_approve_unauth");
    assertNotNull(reg);

    String token = generator.getLastToken();
    assertNotNull(token);

    confirmRegistrationRequest(token);

    // @formatter:off
    mvc.perform(post("/registration/{uuid}/{decision}", reg.getUuid(), APPROVED.name())
        .with(authentication(anonymousAuthenticationToken())))
      .andExpect(status().isUnauthorized());
    // @formatter:on
  }

  @Test
  public void testUsernameAvailable() throws Exception {
    String username = "tester";
    // @formatter:off
    mvc.perform(get("/registration/username-available/{username}", username))
      .andExpect(status().isOk())
      .andExpect(content().string("true"));
    // @formatter:on
  }

  @Test
  public void testUsernameAlreadyTaken() throws Exception {
    String username = "admin";
    // @formatter:off
    mvc.perform(get("/registration/username-available/{username}", username))
      .andExpect(status().isOk())
      .andExpect(content().string("false"));
    // @formatter:on
  }

  @Test
  public void testCreateRequestWithoutNotes() throws Exception {

    String username = "user_with_empty_notes";
    String email = username + "@example.org";

    RegistrationRequestDto request = new RegistrationRequestDto();
    request.setGivenname("Test");
    request.setFamilyname("User");
    request.setEmail(email);
    request.setUsername(username);
    request.setPassword("password");

    // @formatter:off
    mvc.perform(post("/registration/create")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
      .andExpect(status().isBadRequest());
    // @formatter:on
  }

  @Test
  public void testCreateRequestBlankNotes() throws Exception {

    String username = "user_with_empty_notes";
    String email = username + "@example.org";

    RegistrationRequestDto request = new RegistrationRequestDto();
    request.setGivenname("Test");
    request.setFamilyname("User");
    request.setEmail(email);
    request.setUsername(username);
    request.setPassword("password");
    request.setNotes(" ");

    // @formatter:off
    mvc.perform(post("/registration/create")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
      .andExpect(status().isBadRequest());
    // @formatter:on
  }


  private Authentication anonymousAuthenticationToken() {
    return new AnonymousAuthenticationToken("key", "anonymous",
        AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS"));
  }

  private RegistrationRequestDto createRegistrationRequest(String username) throws Exception {

    String email = username + "@example.org";
    RegistrationRequestDto request = new RegistrationRequestDto();
    request.setGivenname("Test");
    request.setFamilyname("User");
    request.setEmail(email);
    request.setUsername(username);
    request.setNotes("Some short notes...");
    request.setPassword("password");

    // @formatter:off
    String response = mvc
      .perform(post("/registration/create").contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
      .andExpect(status().isOk())
      .andReturn()
      .getResponse()
      .getContentAsString();
    // @formatter:on

    return objectMapper.readValue(response, RegistrationRequestDto.class);
  }

  private void confirmRegistrationRequest(String confirmationKey) throws Exception {
    // @formatter:off
    mvc.perform(get("/registration/confirm/{token}", confirmationKey))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.status", equalTo(CONFIRMED.name())));
    // @formatter:on
  }
}
