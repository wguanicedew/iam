package it.infn.mw.iam.test.ext_authn.saml.jit_account_provisioning;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opensaml.saml2.core.AuthnRequest;
import org.opensaml.saml2.core.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import it.infn.mw.iam.IamLoginService;
import it.infn.mw.iam.authn.saml.util.Saml2Attribute;
import it.infn.mw.iam.config.saml.IamSamlProperties.IamSamlJITAccountProvisioningProperties;
import it.infn.mw.iam.persistence.model.IamAccount;
import it.infn.mw.iam.persistence.repository.IamAccountRepository;
import it.infn.mw.iam.test.ext_authn.saml.SamlAuthenticationTestSupport;
import it.infn.mw.iam.test.ext_authn.saml.SamlTestConfig;
import it.infn.mw.iam.test.ext_authn.saml.jit_account_provisioning.JitTestConfig.CountAccountCreatedEventsListener;
import it.infn.mw.iam.test.util.saml.SamlUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(
    classes = {IamLoginService.class, SamlTestConfig.class, JitTestConfig.class})
@WebAppConfiguration
@TestPropertySource(properties = {"saml.jit-account-provisioning.enabled=true",
    "saml.jit-account-provisioning.trusted-idps=" + SamlAuthenticationTestSupport.DEFAULT_IDP_ID})
@Transactional
public class SamlJitAccountProvisioningTests extends SamlAuthenticationTestSupport {


  @Autowired
  IamSamlJITAccountProvisioningProperties props;

  @Autowired
  IamAccountRepository accountRepo;

  @Autowired
  CountAccountCreatedEventsListener accountCreatedEventListener;

  @Test
  public void testLoadedConfiguration() {
    Assert.assertTrue(props.getEnabled());
  }

  @Test
  public void testJITAccountProvisionAccountOnlyOnce() throws Throwable {

    MockHttpSession session =
        (MockHttpSession) mvc.perform(MockMvcRequestBuilders.get(samlLoginUrl()))
          .andExpect(MockMvcResultMatchers.status().isOk())
          .andReturn()
          .getRequest()
          .getSession();

    AuthnRequest authnRequest = getAuthnRequestFromSession(session);

    assertThat(authnRequest.getAssertionConsumerServiceURL(),
        Matchers.equalTo("http://localhost:8080/saml/SSO"));

    Response r = buildJitTest1Response(authnRequest);

    session = (MockHttpSession) mvc
      .perform(post(authnRequest.getAssertionConsumerServiceURL())
        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        .param("SAMLResponse", SamlUtils.signAndSerializeToBase64(r))
        .session(session))
      .andExpect(redirectedUrl("/dashboard"))
      .andReturn()
      .getRequest()
      .getSession();

    assertThat(accountCreatedEventListener.getCount(), equalTo(1L));

    mvc.perform(get("/dashboard").session(session))
      .andExpect(status().isOk())
      .andExpect(view().name("iam/dashboard"));

    IamAccount provisionedAccount = accountRepo
      .findBySamlId(DEFAULT_IDP_ID, Saml2Attribute.epuid.getAttributeName(), JIT1_EPUID)
      .orElseThrow(() -> new AssertionError(
          String.format("Expected provisioned account not found for EPUID '%s'", JIT1_EPUID)));

    assertThat(provisionedAccount.getUsername(), equalTo(JIT1_EPUID));
    assertThat(provisionedAccount.getUserInfo().getEmail(), equalTo(JIT1_MAIL));
    assertTrue(provisionedAccount.isActive());
    assertTrue(provisionedAccount.isProvisioned());
    assertThat(provisionedAccount.getUserInfo().getGivenName(), equalTo(JIT1_GIVEN_NAME));
    assertThat(provisionedAccount.getUserInfo().getFamilyName(), equalTo(JIT1_FAMILY_NAME));

    session = (MockHttpSession) mvc.perform(MockMvcRequestBuilders.get(samlLoginUrl()))
      .andExpect(MockMvcResultMatchers.status().isOk())
      .andReturn()
      .getRequest()
      .getSession();

    authnRequest = getAuthnRequestFromSession(session);

    assertThat(authnRequest.getAssertionConsumerServiceURL(),
        Matchers.equalTo("http://localhost:8080/saml/SSO"));

    r = buildJitTest1Response(authnRequest);

    session = (MockHttpSession) mvc
      .perform(post(authnRequest.getAssertionConsumerServiceURL())
        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        .param("SAMLResponse", SamlUtils.signAndSerializeToBase64(r))
        .session(session))
      .andExpect(redirectedUrl("/dashboard"))
      .andReturn()
      .getRequest()
      .getSession();

    IamAccount newProvisionedAccount = accountRepo
      .findBySamlId(DEFAULT_IDP_ID, Saml2Attribute.epuid.getAttributeName(), JIT1_EPUID)
      .orElseThrow(() -> new AssertionError(
          String.format("Expected provisioned account not found for EPUID '%s'", JIT1_EPUID)));

    assertThat(newProvisionedAccount.getUuid(), equalTo(provisionedAccount.getUuid()));
    assertThat(accountCreatedEventListener.getCount(), equalTo(1L));

  }

}
