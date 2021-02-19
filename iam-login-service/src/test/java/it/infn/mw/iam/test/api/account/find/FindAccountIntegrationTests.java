package it.infn.mw.iam.test.api.account.find;

import static it.infn.mw.iam.api.account.find.FindAccountController.FIND_BY_EMAIL_RESOURCE;
import static it.infn.mw.iam.api.account.find.FindAccountController.FIND_BY_LABEL_RESOURCE;
import static it.infn.mw.iam.api.account.find.FindAccountController.FIND_BY_USERNAME_RESOURCE;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.emptyIterable;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.util.function.Supplier;

import org.junit.After;
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
import it.infn.mw.iam.core.user.IamAccountService;
import it.infn.mw.iam.persistence.model.IamAccount;
import it.infn.mw.iam.persistence.model.IamLabel;
import it.infn.mw.iam.persistence.repository.IamAccountRepository;
import it.infn.mw.iam.test.api.TestSupport;
import it.infn.mw.iam.test.core.CoreControllerTestSupport;
import it.infn.mw.iam.test.util.WithAnonymousUser;
import it.infn.mw.iam.test.util.oauth.MockOAuth2Filter;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {IamLoginService.class, CoreControllerTestSupport.class})
@WebAppConfiguration
@Transactional
@WithMockUser(username = "admin", roles = "ADMIN")
public class FindAccountIntegrationTests extends TestSupport {

  @Autowired
  private IamAccountRepository repo;

  @Autowired
  private IamAccountService accountService;

  @Autowired
  private WebApplicationContext context;

  private MockMvc mvc;

  @Autowired
  private MockOAuth2Filter mockOAuth2Filter;

  @Before
  public void setup() {

    mvc =
        MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).alwaysDo(log()).build();
    mockOAuth2Filter.cleanupSecurityContext();
  }

  @After
  public void cleanupOAuthUser() {
    mockOAuth2Filter.cleanupSecurityContext();
  }

  private Supplier<AssertionError> assertionError(String message) {
    return () -> new AssertionError(message);
  }

  @Test
  @WithAnonymousUser
  public void findingRequiresAuthenticatedUser() throws Exception {

    mvc.perform(get(FIND_BY_LABEL_RESOURCE).param("name", "test").param("value", "test"))
      .andExpect(UNAUTHORIZED);
    mvc.perform(get(FIND_BY_EMAIL_RESOURCE).param("email", "test@example")).andExpect(UNAUTHORIZED);
    mvc.perform(get(FIND_BY_USERNAME_RESOURCE).param("username", "test")).andExpect(UNAUTHORIZED);

  }

  @Test
  @WithMockUser(username = "test", roles = "USER")
  public void findingRequiresAdminUser() throws Exception {

    mvc.perform(get(FIND_BY_LABEL_RESOURCE).param("name", "test").param("value", "test"))
      .andExpect(FORBIDDEN);

    mvc.perform(get(FIND_BY_EMAIL_RESOURCE).param("email", "test@example")).andExpect(FORBIDDEN);
    mvc.perform(get(FIND_BY_USERNAME_RESOURCE).param("username", "test")).andExpect(FORBIDDEN);

  }

  @Test
  public void findByLabelWorks() throws Exception {

    IamAccount testAccount =
        repo.findByUsername(TEST_USER).orElseThrow(assertionError(EXPECTED_ACCOUNT_NOT_FOUND));

    mvc.perform(get(FIND_BY_LABEL_RESOURCE).param("name", "test").param("value", "test"))
      .andExpect(OK)
      .andExpect(jsonPath("$.totalResults", is(0)))
      .andExpect(jsonPath("$.Resources", emptyIterable()));

    IamLabel testLabel = IamLabel.builder().name("test").build();
    accountService.setLabel(testAccount, testLabel);

    mvc.perform(get(FIND_BY_LABEL_RESOURCE).param("name", "test").param("value", "test"))
      .andExpect(OK)
      .andExpect(jsonPath("$.totalResults", is(0)))
      .andExpect(jsonPath("$.Resources", emptyIterable()));

    testLabel = IamLabel.builder().name("test").value("test").build();
    testAccount.getLabels().add(testLabel);
    accountService.setLabel(testAccount, testLabel);

    mvc.perform(get(FIND_BY_LABEL_RESOURCE).param("name", "test").param("value", "test"))
      .andExpect(OK)
      .andExpect(jsonPath("$.totalResults", is(1)))
      .andExpect(jsonPath("$.Resources[0].id", is(testAccount.getUuid())));

    mvc.perform(get(FIND_BY_LABEL_RESOURCE).param("name", "toast").param("value", "test"))
      .andExpect(OK)
      .andExpect(jsonPath("$.totalResults", is(0)))
      .andExpect(jsonPath("$.Resources", emptyIterable()));
  }

  @Test
  public void findByEmailWorks() throws Exception {

    IamAccount testAccount =
        repo.findByUsername(TEST_USER).orElseThrow(assertionError(EXPECTED_ACCOUNT_NOT_FOUND));

    String email = testAccount.getUserInfo().getEmail();

    mvc.perform(get(FIND_BY_EMAIL_RESOURCE).param("email", email))
      .andExpect(OK)
      .andExpect(jsonPath("$.totalResults", is(1)))
      .andExpect(jsonPath("$.Resources[0].id", is(testAccount.getUuid())));

    mvc.perform(get(FIND_BY_EMAIL_RESOURCE).param("email", "not_found@example"))
      .andExpect(OK)
      .andExpect(jsonPath("$.totalResults").doesNotExist())
      .andExpect(jsonPath("$.Resources", emptyIterable()));

  }

  @Test
  public void findByUsernameWorks() throws Exception {

    IamAccount testAccount =
        repo.findByUsername(TEST_USER).orElseThrow(assertionError(EXPECTED_ACCOUNT_NOT_FOUND));

    mvc.perform(get(FIND_BY_USERNAME_RESOURCE).param("username", testAccount.getUsername()))
      .andExpect(OK)
      .andExpect(jsonPath("$.totalResults", is(1)))
      .andExpect(jsonPath("$.Resources[0].id", is(testAccount.getUuid())));

    mvc.perform(get(FIND_BY_USERNAME_RESOURCE).param("username", "unknown_username"))
      .andExpect(OK)
      .andExpect(jsonPath("$.totalResults").doesNotExist())
      .andExpect(jsonPath("$.Resources", emptyIterable()));

  }
}
