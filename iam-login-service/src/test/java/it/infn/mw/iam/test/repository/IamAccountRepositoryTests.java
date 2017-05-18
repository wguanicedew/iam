package it.infn.mw.iam.test.repository;

import static org.hamcrest.Matchers.equalTo;

import javax.persistence.EntityManager;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import it.infn.mw.iam.IamLoginService;
import it.infn.mw.iam.authn.saml.util.Saml2Attribute;
import it.infn.mw.iam.persistence.model.IamAccount;
import it.infn.mw.iam.persistence.model.IamSamlId;
import it.infn.mw.iam.persistence.repository.IamAccountRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {IamLoginService.class})
public class IamAccountRepositoryTests {

  static final IamSamlId TEST_USER_ID = new IamSamlId("https://idptestbed/idp/shibboleth",
      Saml2Attribute.epuid.getAttributeName(), "78901@idptestbed");

  @Autowired
  IamAccountRepository repo;

  @Autowired
  EntityManager em;

  @Test
  public void testSamlIdResolutionWorksAsExpected() {

    IamAccount testUserAccount = repo.findBySamlId(TEST_USER_ID)
      .orElseThrow(() -> new AssertionError("Could not lookup test user by SAML id"));

    Assert.assertThat(testUserAccount.getUsername(), equalTo("test"));
  }

}
