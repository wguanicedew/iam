package it.infn.mw.iam.test.scim.new_updater;

import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.infn.mw.iam.api.scim.converter.OidcIdConverter;
import it.infn.mw.iam.persistence.model.IamOidcId;
import it.infn.mw.iam.persistence.repository.IamAccountRepository;
import it.infn.mw.iam.test.util.JacksonUtils;

@RunWith(MockitoJUnitRunner.class)
public class ParserTests {

  public static final String OLD = "old";
  public static final String NEW = "new";

  public static final IamOidcId OLD_ID = new IamOidcId(OLD, OLD);
  public static final IamOidcId NEW_ID = new IamOidcId(NEW, NEW);

  ObjectMapper mapper = JacksonUtils.createJacksonObjectMapper();

  PasswordEncoder encoder = new BCryptPasswordEncoder();

  @Mock
  IamAccountRepository repo;

  OidcIdConverter oidcConverter;



  // @Test
  // public void testGivenNamePatchOpParsing() {
  //
  // Parser parser = new ParserImpl();
  //
  // IamAccount account = new IamAccount();
  //
  // account.setUserInfo(new IamUserInfo());
  // account.getUserInfo().setGivenName(OLD);
  //
  // ScimUser user = ScimUser.builder().buildName(NEW, NEW).build();
  //
  // ScimUserPatchRequest req = ScimUserPatchRequest.builder().add(user).build();
  //
  // ScimPatchOperation<ScimUser> op = req.getOperations().get(0);
  //
  // List<Updater> updaters = parser.getUpdatersForRequest(account, op);
  //
  // Assert.assertThat(updaters.size(), Matchers.equalTo(1));
  //
  // Assert.assertThat(updaters.get(0).update(), Matchers.equalTo(true));
  // Assert.assertThat(account.getUserInfo().getGivenName(), Matchers.equalTo(NEW));
  //
  // }

}
