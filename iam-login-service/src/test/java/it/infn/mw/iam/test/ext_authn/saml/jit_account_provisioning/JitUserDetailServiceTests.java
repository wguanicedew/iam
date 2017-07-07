package it.infn.mw.iam.test.ext_authn.saml.jit_account_provisioning;

import static it.infn.mw.iam.test.ext_authn.saml.SamlAuthenticationTestSupport.DEFAULT_IDP_ID;
import static it.infn.mw.iam.test.ext_authn.saml.SamlAuthenticationTestSupport.T1_EPUID;
import static it.infn.mw.iam.test.ext_authn.saml.SamlAuthenticationTestSupport.T1_GIVEN_NAME;
import static it.infn.mw.iam.test.ext_authn.saml.SamlAuthenticationTestSupport.T1_MAIL;
import static it.infn.mw.iam.test.ext_authn.saml.SamlAuthenticationTestSupport.T1_SN;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.saml.SAMLCredential;

import com.google.common.collect.Sets;

import it.infn.mw.iam.authn.InactiveAccountAuthenticationHander;
import it.infn.mw.iam.authn.saml.JustInTimeProvisioningSAMLUserDetailsService;
import it.infn.mw.iam.authn.saml.util.Saml2Attribute;
import it.infn.mw.iam.authn.saml.util.SamlUserIdentifierResolver;
import it.infn.mw.iam.core.user.IamAccountService;
import it.infn.mw.iam.persistence.model.IamAccount;
import it.infn.mw.iam.persistence.repository.IamAccountRepository;
import it.infn.mw.iam.test.ext_authn.saml.SamlAuthenticationTestSupport;

@RunWith(MockitoJUnitRunner.class)
public class JitUserDetailServiceTests extends JitUserDetailsServiceTestsSupport {

  @Mock
  private IamAccountRepository accountRepo;

  @Mock
  private IamAccountService accountService;

  @Mock
  private SamlUserIdentifierResolver resolver;

  @Mock
  private InactiveAccountAuthenticationHander inactiveAccountHander;

  private JustInTimeProvisioningSAMLUserDetailsService userDetailsService;

  @Mock
  private SAMLCredential cred;

  @Before
  public void setup() {
    when(accountRepo.findBySamlId(anyObject())).thenReturn(Optional.empty());
    when(accountRepo.findBySamlId(anyString(), anyString(), anyString()))
      .thenReturn(Optional.empty());

    when(accountService.createAccount(anyObject())).thenAnswer(invocation -> {
      IamAccount account = (IamAccount) invocation.getArguments()[0];
      account.setPassword("password");
      return account;
    });

    when(resolver.getSamlUserIdentifier(anyObject())).thenReturn(Optional.empty());

    userDetailsService = new JustInTimeProvisioningSAMLUserDetailsService(resolver, accountService,
        inactiveAccountHander, accountRepo, Optional.empty());
  }

  @Test(expected = NullPointerException.class)
  public void testNullSamlCredential() {
    try {
      userDetailsService.loadUserBySAML(null);
    } catch (NullPointerException e) {
      Assert.assertThat(e.getMessage(), equalTo("null saml credential"));
      throw e;
    }
  }

  @Test(expected = UsernameNotFoundException.class)
  public void testUnresolvedSamlIdSanityChecks() {

    try {
      userDetailsService.loadUserBySAML(cred);
    } catch (UsernameNotFoundException e) {
      assertThat(e.getMessage(),
          equalTo("Could not extract a user identifier from the SAML assertion"));
      throw e;
    }
  }

  @Test(expected = UsernameNotFoundException.class)
  public void testMissingEmailSamlCredentialSanityCheck() {
    when(resolver.getSamlUserIdentifier(cred)).thenReturn(Optional.of(T1_SAML_ID));
    try {
      userDetailsService.loadUserBySAML(cred);
    } catch (UsernameNotFoundException e) {

      assertThat(e.getMessage(), containsString(String.format("missing required attribute: %s (%s)",
          Saml2Attribute.mail.getAlias(), Saml2Attribute.mail.getAttributeName())));
      throw e;
    }
  }

  @Test(expected = UsernameNotFoundException.class)
  public void testMissingGivenNameSamlCredentialSanityCheck() {
    when(resolver.getSamlUserIdentifier(cred)).thenReturn(Optional.of(T1_SAML_ID));
    when(cred.getAttributeAsString(Saml2Attribute.mail.getAttributeName())).thenReturn(T1_MAIL);

    try {
      userDetailsService.loadUserBySAML(cred);
    } catch (UsernameNotFoundException e) {

      assertThat(e.getMessage(), containsString(String.format("missing required attribute: %s (%s)",
          Saml2Attribute.givenName.getAlias(), Saml2Attribute.givenName.getAttributeName())));
      throw e;
    }
  }

  @Test(expected = UsernameNotFoundException.class)
  public void testMissingFamilyNameSamlCredentialSanityCheck() {
    when(resolver.getSamlUserIdentifier(cred)).thenReturn(Optional.of(T1_SAML_ID));
    when(cred.getAttributeAsString(Saml2Attribute.mail.getAttributeName())).thenReturn(T1_MAIL);
    when(cred.getAttributeAsString(Saml2Attribute.givenName.getAttributeName()))
      .thenReturn(T1_GIVEN_NAME);

    try {
      userDetailsService.loadUserBySAML(cred);
    } catch (UsernameNotFoundException e) {

      assertThat(e.getMessage(), containsString(String.format("missing required attribute: %s (%s)",
          Saml2Attribute.sn.getAlias(), Saml2Attribute.sn.getAttributeName())));
      throw e;
    }
  }

  @Test
  public void testSamlIdIsUsedForUsername() {
    when(resolver.getSamlUserIdentifier(cred)).thenReturn(Optional.of(T1_SAML_ID));
    when(cred.getAttributeAsString(Saml2Attribute.mail.getAttributeName())).thenReturn(T1_MAIL);
    when(cred.getAttributeAsString(Saml2Attribute.givenName.getAttributeName()))
      .thenReturn(T1_GIVEN_NAME);
    when(cred.getAttributeAsString(Saml2Attribute.sn.getAttributeName())).thenReturn(T1_SN);

    User user = (User) userDetailsService.loadUserBySAML(cred);
    assertThat(user.getUsername(), equalTo(T1_EPUID));

  }

  @Test
  public void uuidIsUsedForAccountUsernameIfResolvedIdLongerThan128Chars() {
    when(resolver.getSamlUserIdentifier(cred)).thenReturn(Optional.of(LONG_SAML_ID));
    when(cred.getAttributeAsString(Saml2Attribute.mail.getAttributeName())).thenReturn(T1_MAIL);
    when(cred.getAttributeAsString(Saml2Attribute.givenName.getAttributeName()))
      .thenReturn(T1_GIVEN_NAME);
    when(cred.getAttributeAsString(Saml2Attribute.sn.getAttributeName())).thenReturn(T1_SN);

    User user = (User) userDetailsService.loadUserBySAML(cred);
    assertThat(user.getUsername().length(), equalTo(36));
  }

  @Test(expected = UsernameNotFoundException.class)
  public void testEntityIdSanityChecksWorkForUntrustedIdp() {
    Set<String> trustedIdps = Sets.newHashSet("http://trusted.idp.example");
    userDetailsService = new JustInTimeProvisioningSAMLUserDetailsService(resolver, accountService,
        inactiveAccountHander, accountRepo, Optional.of(trustedIdps));

    when(resolver.getSamlUserIdentifier(cred)).thenReturn(Optional.of(T1_SAML_ID));
    when(cred.getRemoteEntityID()).thenReturn(SamlAuthenticationTestSupport.DEFAULT_IDP_ID);
    when(cred.getAttributeAsString(Saml2Attribute.mail.getAttributeName())).thenReturn(T1_MAIL);
    when(cred.getAttributeAsString(Saml2Attribute.givenName.getAttributeName()))
      .thenReturn(T1_GIVEN_NAME);
    when(cred.getAttributeAsString(Saml2Attribute.sn.getAttributeName())).thenReturn(T1_SN);
    try {
      User user = (User) userDetailsService.loadUserBySAML(cred);
    } catch (UsernameNotFoundException e) {
      assertThat(e.getMessage(),
          containsString(String.format("SAML credential issuer '%s' is not trusted",
              SamlAuthenticationTestSupport.DEFAULT_IDP_ID)));
      throw e;
    }
  }
  
  @Test
  public void testEntityIdSanityChecksWorkForTrustedIdp() {
    Set<String> trustedIdps = Sets.newHashSet("http://trusted.idp.example", DEFAULT_IDP_ID);
    userDetailsService = new JustInTimeProvisioningSAMLUserDetailsService(resolver, accountService,
        inactiveAccountHander, accountRepo, Optional.of(trustedIdps));

    when(resolver.getSamlUserIdentifier(cred)).thenReturn(Optional.of(T1_SAML_ID));
    when(cred.getRemoteEntityID()).thenReturn(SamlAuthenticationTestSupport.DEFAULT_IDP_ID);
    when(cred.getAttributeAsString(Saml2Attribute.mail.getAttributeName())).thenReturn(T1_MAIL);
    when(cred.getAttributeAsString(Saml2Attribute.givenName.getAttributeName()))
      .thenReturn(T1_GIVEN_NAME);
    when(cred.getAttributeAsString(Saml2Attribute.sn.getAttributeName())).thenReturn(T1_SN);
    
    User user = (User) userDetailsService.loadUserBySAML(cred);
    assertThat(user.getUsername(), equalTo(T1_EPUID));
    
  }

}
