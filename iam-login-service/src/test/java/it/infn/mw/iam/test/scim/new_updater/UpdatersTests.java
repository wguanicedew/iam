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
import it.infn.mw.iam.api.scim.new_updater.Updaters.Adders;
import it.infn.mw.iam.persistence.model.IamAccount;
import it.infn.mw.iam.persistence.model.IamOidcId;
import it.infn.mw.iam.persistence.model.IamUserInfo;
import it.infn.mw.iam.persistence.repository.IamAccountRepository;

@RunWith(MockitoJUnitRunner.class)
public class UpdatersTests {

  public static final String OLD = "old";
  public static final String NEW = "new";

  public static final IamOidcId OLD_ID = new IamOidcId(OLD, OLD);
  public static final IamOidcId NEW_ID = new IamOidcId(NEW, NEW);

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


  private Adders addToAccount() {
    return Updaters.adders(repo, encoder, account);
  }

  @Before
  public void before() {
    account = newAccount("account");
    other = newAccount("other");

    Mockito.when(repo.findByOidcId(Matchers.anyString(), Matchers.anyString()))
      .thenReturn(Optional.empty());

    Mockito.when(repo.findByEmail(Matchers.anyString())).thenReturn(Optional.empty());
  }

  private void repoBindOidcIdToAccount(IamOidcId id, IamAccount a) {

    Mockito.when(repo.findByOidcId(id.getIssuer(), id.getSubject())).thenReturn(Optional.of(a));
  }

  private void repoBindEmailToAccount(String email, IamAccount a) {

    Mockito.when(repo.findByEmailWithDifferentUUID(email, account.getUuid()))
      .thenReturn(Optional.of(a));
  }

  @Test
  public void testPasswordUpdaterWorks() {

    account.setPassword(encoder.encode(OLD));

    Updater u = addToAccount().password(NEW);

    assertThat(u.update(), is(true));
    assertThat(u.update(), is(false));

  }

  @Test
  public void testGivenNameUpdaterWorks() {

    account.setUserInfo(new IamUserInfo());
    account.getUserInfo().setGivenName(OLD);

    Updater u = addToAccount().givenName(NEW);

    assertThat(u.update(), is(true));
    assertThat(u.update(), is(false));
  }

  @Test
  public void testFamilyNameUpdaterWorks() {

    account.getUserInfo().setFamilyName(OLD);

    Updater u = addToAccount().familyName(NEW);

    assertThat(u.update(), is(true));
    assertThat(u.update(), is(false));
  }

  @Test(expected = ScimResourceExistsException.class)
  public void testEmailUpdaterFailsWhenEmailIsBoundToAnotherUser() {

    other.getUserInfo().setEmail(NEW);
    repoBindEmailToAccount(NEW, other);

    account.getUserInfo().setEmail(OLD);

    addToAccount().email(NEW).update();
  }

  @Test
  public void testEmailUpdaterWorks() {

    account.getUserInfo().setEmail(OLD);

    Mockito.when(repo.findByEmailWithDifferentUUID(NEW, account.getUuid()))
      .thenReturn(Optional.empty());

    Updater u = addToAccount().email(NEW);
    assertThat(u.update(), is(true));
    assertThat(u.update(), is(false));

  }

  @Test
  public void testPictureUpdaterWorks() {
    account.getUserInfo().setPicture(OLD);

    Updater u = addToAccount().picture(NEW);

    assertThat(u.update(), is(true));
    assertThat(u.update(), is(false));
  }

  @Test
  public void testPictureUpdaterWorksForNullValue() {
    account.getUserInfo().setPicture(OLD);

    Updater u = addToAccount().picture(null);

    assertThat(u.update(), is(true));
    assertThat(u.update(), is(false));
  }

  @Test
  public void testOidcIdUpdaterWorks() {

    Updater u = addToAccount().oidcId(Lists.newArrayList(NEW_ID));

    assertThat(u.update(), is(true));
    assertThat(u.update(), is(false));

  }

  @Test
  public void testOidcIdUpdaterWorksWithNoUpdate() {

    account.getOidcIds().add(NEW_ID);
    repoBindOidcIdToAccount(NEW_ID, account);

    Updater u = addToAccount().oidcId(Lists.newArrayList(NEW_ID));

    assertThat(u.update(), is(false));

  }

  @Test(expected = ScimResourceExistsException.class)
  public void testOidcIdUpdaterFailsWhenOidcIdIsLinkedToAnotherAccount() {

    repoBindOidcIdToAccount(NEW_ID, other);
    addToAccount().oidcId(Lists.newArrayList(NEW_ID)).update();

  }

  @Test
  public void testOidcIdUpdaterWorksWithUpdate() {

    repoBindOidcIdToAccount(NEW_ID, account);

    Updater u = addToAccount().oidcId(newArrayList(NEW_ID, OLD_ID));

    assertThat(u.update(), is(true));
    assertThat(account.getOidcIds(), hasSize(2));
    assertThat(account.getOidcIds(), hasItems(NEW_ID, OLD_ID));

    repoBindOidcIdToAccount(OLD_ID, account);

    assertThat(u.update(), is(false));
    assertThat(account.getOidcIds(), hasSize(2));
    assertThat(account.getOidcIds(), hasItems(NEW_ID, OLD_ID));

  }

  @Test
  public void testOidcIdUpdaterWorksWithListContainingNull() {

    account.getOidcIds().add(NEW_ID);
    repoBindOidcIdToAccount(NEW_ID, account);

    Updater u = addToAccount().oidcId(newArrayList(NEW_ID, null));

    assertThat(u.update(), is(false));
    assertThat(account.getOidcIds(), hasSize(1));
    assertThat(account.getOidcIds(), hasItems(NEW_ID));

  }

  @Test
  public void testOidcIdUpdaterWorksWithListContainingDuplicates() {

    repoBindOidcIdToAccount(NEW_ID, account);

    Updater u = addToAccount().oidcId(newArrayList(NEW_ID, NEW_ID, OLD_ID));

    assertThat(u.update(), is(true));
    assertThat(account.getOidcIds(), hasSize(2));
    assertThat(account.getOidcIds(), hasItems(NEW_ID, OLD_ID));

  }

}
