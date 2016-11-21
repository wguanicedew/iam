package it.infn.mw.iam.test.scim.new_updater;


import static com.google.common.collect.Lists.newArrayList;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.Optional;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.google.common.collect.Lists;

import it.infn.mw.iam.api.scim.exception.ScimResourceExistsException;
import it.infn.mw.iam.api.scim.new_updater.Updater;
import it.infn.mw.iam.api.scim.new_updater.Updaters;
import it.infn.mw.iam.api.scim.new_updater.builders.Adders;
import it.infn.mw.iam.api.scim.new_updater.builders.Removers;
import it.infn.mw.iam.persistence.model.IamAccount;
import it.infn.mw.iam.persistence.model.IamOidcId;
import it.infn.mw.iam.persistence.model.IamSamlId;
import it.infn.mw.iam.persistence.model.IamUserInfo;
import it.infn.mw.iam.persistence.repository.IamAccountRepository;

@RunWith(MockitoJUnitRunner.class)
public class UpdatersTests {

  public static final String OLD = "old";
  public static final String NEW = "new";

  public static final IamOidcId OLD_OIDC_ID = new IamOidcId(OLD, OLD);
  public static final IamOidcId NEW_OIDC_ID = new IamOidcId(NEW, NEW);

  public static final IamSamlId OLD_SAML_ID = new IamSamlId(OLD, OLD);
  public static final IamSamlId NEW_SAML_ID = new IamSamlId(NEW, NEW);

  @Mock
  IamAccountRepository repo;

  PasswordEncoder encoder = new BCryptPasswordEncoder();

  IamAccount account;
  IamAccount other;

  private IamAccount newAccount(String username) {
    IamAccount result = new IamAccount();
    result.setUserInfo(new IamUserInfo());

    result.setUsername(username);
    result.setUuid(UUID.randomUUID().toString());
    return result;
  }


  private Adders adder() {
    return Updaters.adders(repo, encoder, account);
  }

  private Removers removers() {
    return Updaters.removers(repo, account);
  }

  @Before
  public void before() {
    account = newAccount("account");
    other = newAccount("other");

    Mockito.when(repo.findByOidcId(Matchers.anyString(), Matchers.anyString()))
      .thenReturn(Optional.empty());

    Mockito.when(repo.findBySamlId(Matchers.anyString(), Matchers.anyString()))
      .thenReturn(Optional.empty());

    Mockito.when(repo.findByEmail(Matchers.anyString())).thenReturn(Optional.empty());
  }

  private void repoBindOidcIdToAccount(IamOidcId id, IamAccount a) {

    Mockito.when(repo.findByOidcId(id.getIssuer(), id.getSubject())).thenReturn(Optional.of(a));
  }

  private void repoBindSamlIdToAccount(IamSamlId id, IamAccount a) {

    Mockito.when(repo.findBySamlId(id.getIdpId(), id.getUserId())).thenReturn(Optional.of(a));
  }

  private void repoBindEmailToAccount(String email, IamAccount a) {

    Mockito.when(repo.findByEmail(email)).thenReturn(Optional.of(a));
  }

  @Test
  public void testPasswordAdderWorks() {

    account.setPassword(encoder.encode(OLD));

    Updater u = adder().password(NEW);

    assertThat(u.update(), is(true));
    assertThat(u.update(), is(false));

  }

  @Test
  public void testGivenNameAdderWorks() {

    account.setUserInfo(new IamUserInfo());
    account.getUserInfo().setGivenName(OLD);

    Updater u = adder().givenName(NEW);

    assertThat(u.update(), is(true));
    assertThat(u.update(), is(false));
  }

  @Test
  public void testFamilyNameAdderWorks() {

    account.getUserInfo().setFamilyName(OLD);

    Updater u = adder().familyName(NEW);

    assertThat(u.update(), is(true));
    assertThat(u.update(), is(false));
  }

  @Test(expected = ScimResourceExistsException.class)
  public void testEmailAdderFailsWhenEmailIsBoundToAnotherUser() {

    account.getUserInfo().setEmail(OLD);
    repoBindEmailToAccount(OLD, account);

    other.getUserInfo().setEmail(NEW);
    repoBindEmailToAccount(NEW, other);

    adder().email(NEW).update();
  }

  @Test
  public void testEmailAdderWorks() {

    account.getUserInfo().setEmail(OLD);

    Mockito.when(repo.findByEmailWithDifferentUUID(NEW, account.getUuid()))
      .thenReturn(Optional.empty());

    Updater u = adder().email(NEW);
    assertThat(u.update(), is(true));
    assertThat(u.update(), is(false));

  }

  @Test
  public void testPictureAdderWorks() {
    account.getUserInfo().setPicture(OLD);

    Updater u = adder().picture(NEW);

    assertThat(u.update(), is(true));
    assertThat(u.update(), is(false));
  }

  @Test
  public void testPictureAdderWorksForNullValue() {
    account.getUserInfo().setPicture(OLD);

    Updater u = adder().picture(null);

    assertThat(u.update(), is(true));
    assertThat(u.update(), is(false));
  }

  @Test
  public void testOidcIdAdderWorks() {

    Updater u = adder().oidcId(Lists.newArrayList(NEW_OIDC_ID));

    assertThat(u.update(), is(true));
    assertThat(u.update(), is(false));

    assertThat(account.getOidcIds(), hasSize(1));
    assertThat(account.getOidcIds(), hasItems(NEW_OIDC_ID));

  }

  @Test
  public void testOidcIdAdderWorksWithNoUpdate() {

    account.getOidcIds().add(NEW_OIDC_ID);
    repoBindOidcIdToAccount(NEW_OIDC_ID, account);

    Updater u = adder().oidcId(Lists.newArrayList(NEW_OIDC_ID));

    assertThat(u.update(), is(false));

  }

  @Test(expected = ScimResourceExistsException.class)
  public void testOidcIdAdderFailsWhenOidcIdIsLinkedToAnotherAccount() {

    repoBindOidcIdToAccount(NEW_OIDC_ID, other);
    adder().oidcId(Lists.newArrayList(NEW_OIDC_ID)).update();

  }

  @Test
  public void testOidcIdAdderWorksWithUpdate() {

    repoBindOidcIdToAccount(NEW_OIDC_ID, account);

    Updater u = adder().oidcId(newArrayList(NEW_OIDC_ID, OLD_OIDC_ID));

    assertThat(u.update(), is(true));
    assertThat(account.getOidcIds(), hasSize(2));
    assertThat(account.getOidcIds(), hasItems(NEW_OIDC_ID, OLD_OIDC_ID));

    repoBindOidcIdToAccount(OLD_OIDC_ID, account);

    assertThat(u.update(), is(false));
    assertThat(account.getOidcIds(), hasSize(2));
    assertThat(account.getOidcIds(), hasItems(NEW_OIDC_ID, OLD_OIDC_ID));

  }

  @Test
  public void testOidcIdAdderWorksWithListContainingNull() {

    account.getOidcIds().add(NEW_OIDC_ID);
    repoBindOidcIdToAccount(NEW_OIDC_ID, account);

    Updater u = adder().oidcId(newArrayList(NEW_OIDC_ID, null));

    assertThat(u.update(), is(false));
    assertThat(account.getOidcIds(), hasSize(1));
    assertThat(account.getOidcIds(), hasItems(NEW_OIDC_ID));

  }

  @Test
  public void testOidcIdAdderWorksWithListContainingDuplicates() {

    account.getOidcIds().add(NEW_OIDC_ID);
    repoBindOidcIdToAccount(NEW_OIDC_ID, account);

    Updater u = adder().oidcId(newArrayList(NEW_OIDC_ID, NEW_OIDC_ID, OLD_OIDC_ID));

    assertThat(u.update(), is(true));
    assertThat(account.getOidcIds(), hasSize(2));
    assertThat(account.getOidcIds(), hasItems(NEW_OIDC_ID, OLD_OIDC_ID));

  }


  @Test
  public void testSamlIdAdderWorks() {

    Updater u = adder().samlId(newArrayList(NEW_SAML_ID));

    assertThat(u.update(), is(true));
    assertThat(u.update(), is(false));

    assertThat(account.getSamlIds(), hasSize(1));
    assertThat(account.getSamlIds(), hasItems(NEW_SAML_ID));

  }

  @Test
  public void testSamlIdAdderWorksWithNoUpdate() {

    account.getSamlIds().add(NEW_SAML_ID);
    repoBindSamlIdToAccount(NEW_SAML_ID, account);

    Updater u = adder().samlId(newArrayList(NEW_SAML_ID));

    assertThat(u.update(), is(false));

    assertThat(account.getSamlIds(), hasSize(1));
    assertThat(account.getSamlIds(), hasItems(NEW_SAML_ID));

  }


  @Test(expected = ScimResourceExistsException.class)
  public void testSamlIdAdderFailsWhenSamlIdLinkedToAnotherAccount() {
    other.getSamlIds().add(NEW_SAML_ID);
    repoBindSamlIdToAccount(NEW_SAML_ID, other);

    adder().samlId(newArrayList(NEW_SAML_ID)).update();

  }

  @Test
  public void testSamlAdderWorksWithListContainingNull() {
    account.getSamlIds().add(NEW_SAML_ID);
    repoBindSamlIdToAccount(NEW_SAML_ID, account);

    Updater u = adder().samlId(newArrayList(NEW_SAML_ID, null, null));
    assertThat(u.update(), is(false));

    assertThat(account.getSamlIds(), hasSize(1));
    assertThat(account.getSamlIds(), hasItems(NEW_SAML_ID));

  }

  @Test
  public void testSamlAdderWorksWithListContainingDuplicates() {
    account.getSamlIds().add(NEW_SAML_ID);
    repoBindSamlIdToAccount(NEW_SAML_ID, account);

    Updater u = adder().samlId(newArrayList(NEW_SAML_ID, NEW_SAML_ID, OLD_SAML_ID));
    assertThat(u.update(), is(true));

    assertThat(account.getSamlIds(), hasSize(2));
    assertThat(account.getSamlIds(), hasItems(NEW_SAML_ID, OLD_SAML_ID));

  }


  @Test
  public void testSamlRemoverWorks() {
    account.getSamlIds().add(NEW_SAML_ID);
    repoBindSamlIdToAccount(NEW_SAML_ID, account);

    Updater u = removers().samlId(newArrayList(NEW_SAML_ID));
    assertThat(u.update(), is(true));
    assertThat(account.getSamlIds(), hasSize(0));
  }

  @Test
  public void testSamlRemoverWorksWithNoUpdate() {
    account.getSamlIds().add(NEW_SAML_ID);
    repoBindSamlIdToAccount(NEW_SAML_ID, account);

    Updater u = removers().samlId(newArrayList(OLD_SAML_ID));
    assertThat(u.update(), is(false));
    assertThat(account.getSamlIds(), hasSize(1));
    assertThat(account.getSamlIds(), hasItems(NEW_SAML_ID));
  }

  @Test
  public void testSamlRemoverNoUpdateWithEmptyList() {

    Updater u = removers().samlId(newArrayList(OLD_SAML_ID));
    assertThat(u.update(), is(false));
    assertThat(account.getSamlIds(), hasSize(0));

  }

  @Test
  public void testSamlRemoverNoUpdateWithEmptyList2() {

    Updater u = removers().samlId(newArrayList());
    assertThat(u.update(), is(false));
    assertThat(account.getSamlIds(), hasSize(0));

  }

  @Test
  public void testSamlRemoverWorksWithMultipleValues() {
    account.getSamlIds().add(NEW_SAML_ID);
    account.getSamlIds().add(OLD_SAML_ID);

    Updater u = removers().samlId(newArrayList(NEW_SAML_ID, OLD_SAML_ID));
    assertThat(u.update(), is(true));
    assertThat(account.getSamlIds(), hasSize(0));

  }

  @Test
  public void testSamlRemoverWorksWithNullAndDuplicatesValues() {
    account.getSamlIds().add(NEW_SAML_ID);


    Updater u = removers().samlId(newArrayList(NEW_SAML_ID, OLD_SAML_ID, null, OLD_SAML_ID));
    assertThat(u.update(), is(true));
    assertThat(account.getSamlIds(), hasSize(0));

  }


}
