package it.infn.mw.iam.test.scim.new_updater;

import static org.hamcrest.Matchers.equalTo;

import java.util.List;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.infn.mw.iam.api.scim.converter.OidcIdConverter;
import it.infn.mw.iam.api.scim.converter.SamlIdConverter;
import it.infn.mw.iam.api.scim.model.ScimPatchOperation;
import it.infn.mw.iam.api.scim.model.ScimUser;
import it.infn.mw.iam.api.scim.model.ScimUserPatchRequest;
import it.infn.mw.iam.api.scim.new_updater.DefaultAccountUpdaterFactory;
import it.infn.mw.iam.api.scim.new_updater.Updater;
import it.infn.mw.iam.api.scim.new_updater.UpdaterFactory;
import it.infn.mw.iam.persistence.model.IamAccount;
import it.infn.mw.iam.persistence.model.IamOidcId;
import it.infn.mw.iam.persistence.model.IamUserInfo;
import it.infn.mw.iam.persistence.repository.IamAccountRepository;
import it.infn.mw.iam.test.util.JacksonUtils;

@RunWith(MockitoJUnitRunner.class)
public class UpdaterFactoryTests {

  public static final String OLD = "old";
  public static final String NEW = "new";

  public static final IamOidcId OLD_ID = new IamOidcId(OLD, OLD);
  public static final IamOidcId NEW_ID = new IamOidcId(NEW, NEW);

  ObjectMapper mapper = JacksonUtils.createJacksonObjectMapper();

  PasswordEncoder encoder = new BCryptPasswordEncoder();

  @Mock
  IamAccountRepository repo;

  OidcIdConverter oidcConverter = new OidcIdConverter();
  SamlIdConverter samlConverter = new SamlIdConverter();


  @Test
  public void testGivenNamePatchOpParsing() {

    UpdaterFactory<IamAccount, ScimUser> factory =
        new DefaultAccountUpdaterFactory(encoder, repo, oidcConverter, samlConverter);

    IamAccount account = new IamAccount();

    account.setUserInfo(new IamUserInfo());
    account.getUserInfo().setGivenName(OLD);

    ScimUser user = ScimUser.builder().buildName(NEW, null).build();

    ScimUserPatchRequest req = ScimUserPatchRequest.builder().add(user).build();

    ScimPatchOperation<ScimUser> op = req.getOperations().get(0);

    List<Updater> updaters = factory.getUpdatersForPatchOperation(account, op);

    Assert.assertThat(updaters.size(), Matchers.equalTo(1));

    Assert.assertThat(updaters.get(0).update(), equalTo(true));
    Assert.assertThat(account.getUserInfo().getGivenName(), equalTo(NEW));

  }

}
