package it.infn.mw.iam.test.audit.event;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import javax.transaction.Transactional;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.google.common.collect.Lists;

import it.infn.mw.iam.IamLoginService;
import it.infn.mw.iam.api.scim.converter.ScimResourceLocationProvider;
import it.infn.mw.iam.api.scim.model.ScimGroup;
import it.infn.mw.iam.api.scim.model.ScimGroupPatchRequest;
import it.infn.mw.iam.api.scim.model.ScimMemberRef;
import it.infn.mw.iam.api.scim.model.ScimOidcId;
import it.infn.mw.iam.api.scim.model.ScimSamlId;
import it.infn.mw.iam.api.scim.model.ScimSshKey;
import it.infn.mw.iam.api.scim.model.ScimUser;
import it.infn.mw.iam.api.scim.model.ScimUserPatchRequest;
import it.infn.mw.iam.api.scim.model.ScimX509Certificate;
import it.infn.mw.iam.api.scim.provisioning.ScimGroupProvisioning;
import it.infn.mw.iam.api.scim.provisioning.ScimUserProvisioning;
import it.infn.mw.iam.audit.IamAuditEventLogger;
import it.infn.mw.iam.audit.events.IamAuditApplicationEvent;
import it.infn.mw.iam.audit.events.account.group.GroupMembershipAddedEvent;
import it.infn.mw.iam.audit.events.account.group.GroupMembershipRemovedEvent;
import it.infn.mw.iam.audit.events.account.oidc.OidcAccountAddedEvent;
import it.infn.mw.iam.audit.events.account.oidc.OidcAccountRemovedEvent;
import it.infn.mw.iam.audit.events.account.saml.SamlAccountAddedEvent;
import it.infn.mw.iam.audit.events.account.saml.SamlAccountRemovedEvent;
import it.infn.mw.iam.audit.events.account.ssh.SshKeyAddedEvent;
import it.infn.mw.iam.audit.events.account.ssh.SshKeyRemovedEvent;
import it.infn.mw.iam.audit.events.account.x509.X509CertificateAddedEvent;
import it.infn.mw.iam.audit.events.account.x509.X509CertificateRemovedEvent;
import it.infn.mw.iam.authn.saml.util.SamlAttributeNames;
import it.infn.mw.iam.persistence.model.IamAccount;
import it.infn.mw.iam.test.TestUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {IamLoginService.class})
@WebAppConfiguration
@Transactional
public class EventTests {

  private static final String USERNAME = "event_user";
  private static final String GIVENNAME = "Event";
  private static final String FAMILYNAME = "User";
  private static final String EMAIL = "event_user@localhost";
  private static final String SAML_IDP = "test_idp";
  private static final String SAML_USER_ID = "test_user_id";
  private static final String OIDC_ISSUER = "test_issuer";
  private static final String OIDC_SUBJECT = "test_subject";
  private static final String SSH_LABEL = "test_label";
  private static final String SSH_KEY = TestUtils.sshKeys.get(0).key;
  private static final String SSH_FINGERPRINT = TestUtils.sshKeys.get(0).fingerprintSHA256;
  private static final String X509_LABEL = "test_label";
  private static final String X509_CERT = TestUtils.x509Certs.get(0).certificate;
  private static final String GROUPNAME = "event_group";

  private static final String USERNAME_MESSAGE_CHECK = String.format("username: '%s'", USERNAME);

  @Autowired
  private IamAuditEventLogger logger;

  @Autowired
  private ScimUserProvisioning userProvisioning;

  @Autowired
  private ScimGroupProvisioning groupProvisioning;

  @Autowired
  private ScimResourceLocationProvider scimResourceLocationProvider;

  private IamAccount account;
  private ScimGroup group;
  private ScimMemberRef accountRef;

  @Before
  public void setup() {

    group = groupProvisioning.create(ScimGroup.builder(GROUPNAME).build());

    ScimUser user = ScimUser.builder(USERNAME)
      .buildName(GIVENNAME, FAMILYNAME)
      .buildEmail(EMAIL)
      .buildSamlId(SAML_IDP, SAML_USER_ID)
      .buildOidcId(OIDC_ISSUER, OIDC_SUBJECT)
      .buildSshKey(SSH_LABEL, SSH_KEY, SSH_FINGERPRINT, true)
      .buildX509Certificate(X509_LABEL, X509_CERT, true)
      .build();

    account = userProvisioning.createAccount(user);
    assertNotNull(account);

    accountRef = ScimMemberRef.builder()
      .display(account.getUsername())
      .value(account.getUuid())
      .ref(scimResourceLocationProvider.userLocation(account.getUuid()))
      .build();

    ScimGroupPatchRequest req =
        ScimGroupPatchRequest.builder().add(Lists.newArrayList(accountRef)).build();

    groupProvisioning.update(group.getId(), req.getOperations());
  }

  @After
  public void teardown() {
    userProvisioning.delete(account.getUuid());
    groupProvisioning.delete(group.getId());
  }

  @Test
  public void testAddSamlAccountEvent() {

    ScimUser update = ScimUser.builder()
      .addSamlId(ScimSamlId.builder().attributeId("foo").idpId("bar").userId("test").build())
      .build();

    ScimUserPatchRequest req = ScimUserPatchRequest.builder().add(update).build();
    userProvisioning.update(account.getUuid(), req.getOperations());

    IamAuditApplicationEvent event = logger.getLastEvent();
    assertThat(event, instanceOf(SamlAccountAddedEvent.class));
    assertNotNull(event.getMessage());
    assertThat(event.getMessage(), containsString("Add SAML account to user"));
    assertThat(event.getMessage(), containsString(USERNAME_MESSAGE_CHECK));
    assertThat(event.getMessage(), containsString("idpId=bar"));
    assertThat(event.getMessage(), containsString("attributeId=foo"));
    assertThat(event.getMessage(), containsString("userId=test"));
  }

  @Test
  public void testRemoveSamlAccountEvent() {

    ScimUser update = ScimUser.builder()
      .addSamlId(ScimSamlId.builder().idpId(SAML_IDP).userId(SAML_USER_ID).build())
      .build();

    ScimUserPatchRequest req = ScimUserPatchRequest.builder().remove(update).build();
    userProvisioning.update(account.getUuid(), req.getOperations());

    IamAuditApplicationEvent event = logger.getLastEvent();
    assertThat(event, instanceOf(SamlAccountRemovedEvent.class));
    assertNotNull(event.getMessage());
    assertThat(event.getMessage(), containsString("Remove SAML account from user"));
    assertThat(event.getMessage(), containsString(USERNAME_MESSAGE_CHECK));
    assertThat(event.getMessage(), containsString("idpId=" + SAML_IDP));
    assertThat(event.getMessage(),
        containsString("attributeId=" + SamlAttributeNames.eduPersonUniqueId));
    assertThat(event.getMessage(), containsString("userId=" + SAML_USER_ID));
  }

  @Test
  public void testAddOidcAccountEvent() {

    ScimUser update = ScimUser.builder()
      .addOidcId(ScimOidcId.builder().issuer("foo").subject("bar").build())
      .build();

    ScimUserPatchRequest req = ScimUserPatchRequest.builder().add(update).build();
    userProvisioning.update(account.getUuid(), req.getOperations());

    IamAuditApplicationEvent event = logger.getLastEvent();
    assertThat(event, instanceOf(OidcAccountAddedEvent.class));
    assertNotNull(event.getMessage());
    assertThat(event.getMessage(), containsString("Add OpenID Connect account to user"));
    assertThat(event.getMessage(), containsString(USERNAME_MESSAGE_CHECK));
    assertThat(event.getMessage(), containsString("issuer=foo"));
    assertThat(event.getMessage(), containsString("subject=bar"));
  }

  @Test
  public void testRemoveOidcAccountEvent() {

    ScimUser update = ScimUser.builder()
      .addOidcId(ScimOidcId.builder().issuer(OIDC_ISSUER).subject(OIDC_SUBJECT).build())
      .build();

    ScimUserPatchRequest req = ScimUserPatchRequest.builder().remove(update).build();
    userProvisioning.update(account.getUuid(), req.getOperations());

    IamAuditApplicationEvent event = logger.getLastEvent();
    assertThat(event, instanceOf(OidcAccountRemovedEvent.class));
    assertNotNull(event.getMessage());
    assertThat(event.getMessage(), containsString("Remove OpenID Connect account from user"));
    assertThat(event.getMessage(), containsString(USERNAME_MESSAGE_CHECK));
    assertThat(event.getMessage(), containsString("issuer=" + OIDC_ISSUER));
    assertThat(event.getMessage(), containsString("subject=" + OIDC_SUBJECT));
  }

  @Test
  public void testAddSshKeyEvent() {

    ScimUser update = ScimUser.builder()
      .addSshKey(ScimSshKey.builder()
        .display("foo")
        .value(TestUtils.sshKeys.get(1).key)
        .fingerprint(TestUtils.sshKeys.get(1).fingerprintSHA256)
        .build())
      .build();

    ScimUserPatchRequest req = ScimUserPatchRequest.builder().add(update).build();
    userProvisioning.update(account.getUuid(), req.getOperations());

    IamAuditApplicationEvent event = logger.getLastEvent();
    assertThat(event, instanceOf(SshKeyAddedEvent.class));
    assertNotNull(event.getMessage());
    assertThat(event.getMessage(), containsString("Add ssh key to user"));
    assertThat(event.getMessage(), containsString(USERNAME_MESSAGE_CHECK));
    assertThat(event.getMessage(), containsString("label=foo"));
    assertThat(event.getMessage(),
        containsString("fingerprint=" + TestUtils.sshKeys.get(1).fingerprintSHA256));
    assertThat(event.getMessage(), containsString("value=" + TestUtils.sshKeys.get(1).key));
  }

  @Test
  public void testRemoveSshKeyEvent() {

    ScimUser update = ScimUser.builder()
      .addSshKey(ScimSshKey.builder()
        .display(SSH_LABEL)
        .value(SSH_KEY)
        .fingerprint(SSH_FINGERPRINT)
        .build())
      .build();

    ScimUserPatchRequest req = ScimUserPatchRequest.builder().remove(update).build();
    userProvisioning.update(account.getUuid(), req.getOperations());

    IamAuditApplicationEvent event = logger.getLastEvent();
    assertThat(event, instanceOf(SshKeyRemovedEvent.class));
    assertNotNull(event.getMessage());
    assertThat(event.getMessage(), containsString("Remove ssh key from user"));
    assertThat(event.getMessage(), containsString(USERNAME_MESSAGE_CHECK));
    assertThat(event.getMessage(), containsString("label=" + SSH_LABEL));
    assertThat(event.getMessage(), containsString("fingerprint=" + SSH_FINGERPRINT));
    assertThat(event.getMessage(), containsString("value=" + SSH_KEY));
  }

  @Test
  public void testAddX509CertificateEvent() {

    ScimUser update = ScimUser.builder()
      .addX509Certificate(ScimX509Certificate.builder()
        .display(TestUtils.x509Certs.get(1).display)
        .value(TestUtils.x509Certs.get(1).certificate)
        .build())
      .build();

    ScimUserPatchRequest req = ScimUserPatchRequest.builder().add(update).build();
    userProvisioning.update(account.getUuid(), req.getOperations());

    IamAuditApplicationEvent event = logger.getLastEvent();
    assertThat(event, instanceOf(X509CertificateAddedEvent.class));
    assertNotNull(event.getMessage());
    assertThat(event.getMessage(), containsString("Add x509 certificate to user"));
    assertThat(event.getMessage(), containsString(USERNAME_MESSAGE_CHECK));
    assertThat(event.getMessage(), containsString("label=" + TestUtils.x509Certs.get(1).display));
    assertThat(event.getMessage(),
        containsString("certificate=" + TestUtils.x509Certs.get(1).certificate));
  }

  @Test
  public void testRemoveX509CertificateEvent() {

    ScimUser update = ScimUser.builder()
      .addX509Certificate(
          ScimX509Certificate.builder().display(X509_LABEL).value(X509_CERT).build())
      .build();

    ScimUserPatchRequest req = ScimUserPatchRequest.builder().remove(update).build();
    userProvisioning.update(account.getUuid(), req.getOperations());

    IamAuditApplicationEvent event = logger.getLastEvent();
    assertThat(event, instanceOf(X509CertificateRemovedEvent.class));
    assertNotNull(event.getMessage());
    assertThat(event.getMessage(), containsString("Remove x509 certificate from user"));
    assertThat(event.getMessage(), containsString(USERNAME_MESSAGE_CHECK));
    assertThat(event.getMessage(), containsString("label=" + X509_LABEL));
    assertThat(event.getMessage(), containsString("certificate=" + X509_CERT));
  }

  @Test
  public void testAddGroupMembershipEvent() {

    ScimGroup secondGroup = groupProvisioning.create(ScimGroup.builder("second_group").build());

    ScimGroupPatchRequest req =
        ScimGroupPatchRequest.builder().add(Lists.newArrayList(accountRef)).build();

    groupProvisioning.update(secondGroup.getId(), req.getOperations());

    IamAuditApplicationEvent event = logger.getLastEvent();
    assertThat(event, instanceOf(GroupMembershipAddedEvent.class));
    assertNotNull(event.getMessage());
    assertThat(event.getMessage(), containsString("Add group to user"));
    assertThat(event.getMessage(), containsString(USERNAME_MESSAGE_CHECK));
    assertThat(event.getMessage(), containsString("name=second_group"));
  }

  @Test
  public void testRemoveGroupMembershipEvent() {

    ScimGroupPatchRequest req =
        ScimGroupPatchRequest.builder().remove(Lists.newArrayList(accountRef)).build();

    groupProvisioning.update(group.getId(), req.getOperations());

    IamAuditApplicationEvent event = logger.getLastEvent();
    assertThat(event, instanceOf(GroupMembershipRemovedEvent.class));
    assertNotNull(event.getMessage());
    assertThat(event.getMessage(), containsString("Remove user from group"));
    assertThat(event.getMessage(), containsString(USERNAME_MESSAGE_CHECK));
    assertThat(event.getMessage(), containsString("name=" + GROUPNAME));
  }

}
