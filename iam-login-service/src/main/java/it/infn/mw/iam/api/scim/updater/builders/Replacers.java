package it.infn.mw.iam.api.scim.updater.builders;

import static it.infn.mw.iam.api.scim.updater.UpdaterType.ACCOUNT_REPLACE_ACTIVE;
import static it.infn.mw.iam.api.scim.updater.UpdaterType.ACCOUNT_REPLACE_EMAIL;
import static it.infn.mw.iam.api.scim.updater.UpdaterType.ACCOUNT_REPLACE_FAMILY_NAME;
import static it.infn.mw.iam.api.scim.updater.UpdaterType.ACCOUNT_REPLACE_GIVEN_NAME;
import static it.infn.mw.iam.api.scim.updater.UpdaterType.ACCOUNT_REPLACE_PASSWORD;
import static it.infn.mw.iam.api.scim.updater.UpdaterType.ACCOUNT_REPLACE_PICTURE;
import static it.infn.mw.iam.api.scim.updater.UpdaterType.ACCOUNT_REPLACE_USERNAME;

import java.util.function.Consumer;
import java.util.function.Predicate;

import org.springframework.security.crypto.password.PasswordEncoder;

import it.infn.mw.iam.api.scim.exception.ScimResourceExistsException;
import it.infn.mw.iam.api.scim.updater.AccountEventBuilder;
import it.infn.mw.iam.api.scim.updater.AccountUpdater;
import it.infn.mw.iam.api.scim.updater.DefaultAccountUpdater;
import it.infn.mw.iam.api.scim.updater.util.AccountFinder;
import it.infn.mw.iam.api.scim.updater.util.IdNotBoundChecker;
import it.infn.mw.iam.audit.events.account.ActiveReplacedEvent;
import it.infn.mw.iam.audit.events.account.EmailReplacedEvent;
import it.infn.mw.iam.audit.events.account.FamilyNameReplacedEvent;
import it.infn.mw.iam.audit.events.account.GivenNameReplacedEvent;
import it.infn.mw.iam.audit.events.account.PasswordReplacedEvent;
import it.infn.mw.iam.audit.events.account.PictureReplacedEvent;
import it.infn.mw.iam.audit.events.account.UsernameReplacedEvent;
import it.infn.mw.iam.persistence.model.IamAccount;
import it.infn.mw.iam.persistence.model.IamUserInfo;
import it.infn.mw.iam.persistence.repository.IamAccountRepository;

public class Replacers extends AccountBuilderSupport {

  final Predicate<String> emailAddChecks;
  final Predicate<String> usernameAddChecks;

  final Consumer<String> encodedPasswordSetter;
  final Predicate<String> encodedPasswordChecker;

  final AccountFinder<String> findByEmail;
  final AccountFinder<String> findByUsername;

  final AccountEventBuilder<String, GivenNameReplacedEvent> buildGivenNameReplacedEvent =
      (source, a, v) -> {
        return new GivenNameReplacedEvent(source, a, v);
      };

  final AccountEventBuilder<String, FamilyNameReplacedEvent> buildFamilyNameReplacedEvent =
      (source, a, v) -> {
        return new FamilyNameReplacedEvent(source, a, v);
      };

  final AccountEventBuilder<String, PictureReplacedEvent> buildPictureReplacedEvent =
      (source, a, v) -> {
        return new PictureReplacedEvent(source, a, v);
      };

  final AccountEventBuilder<String, EmailReplacedEvent> buildEmailReplacedEvent =
      (source, a, v) -> {
        return new EmailReplacedEvent(source, a, v);
      };

  final AccountEventBuilder<String, PasswordReplacedEvent> buildPasswordReplacedEvent =
      (source, a, v) -> {
        return new PasswordReplacedEvent(source, a, v);
      };

  final AccountEventBuilder<String, UsernameReplacedEvent> buildUsernameReplacedEvent =
      (source, a, v) -> {
        return new UsernameReplacedEvent(source, a, v);
      };

  final AccountEventBuilder<Boolean, ActiveReplacedEvent> buildActiveReplacedEvent =
      (source, a, v) -> {
        return new ActiveReplacedEvent(source, a, v);
      };


  public Replacers(IamAccountRepository repo, PasswordEncoder encoder, IamAccount account) {

    super(repo, encoder, account);
    findByEmail = e -> repo.findByEmail(e);
    findByUsername = u -> repo.findByUsername(u);
    encodedPasswordSetter = t -> account.setPassword(encoder.encode(t));
    encodedPasswordChecker = t -> !encoder.matches(t, account.getPassword());
    emailAddChecks = buildEmailAddChecks();
    usernameAddChecks = buildUsernameAddChecks();

  }

  private Predicate<String> buildEmailAddChecks() {
    Predicate<String> emailNotBound =
        new IdNotBoundChecker<String>(findByEmail, account, (e, a) -> {
          throw new ScimResourceExistsException("Email " + e + " already bound to another user");
        });

    Predicate<String> emailNotOwned = e -> !account.getUserInfo().getEmail().equals(e);

    return emailNotBound.and(emailNotOwned);
  }

  private Predicate<String> buildUsernameAddChecks() {
    Predicate<String> usernameNotBound =
        new IdNotBoundChecker<String>(findByUsername, account, (e, a) -> {
          throw new ScimResourceExistsException("Username " + e + " already bound to another user");
        });

    Predicate<String> usernameNotOwned = e -> !account.getUsername().equals(e);

    return usernameNotBound.and(usernameNotOwned);
  }

  public AccountUpdater givenName(String givenName) {

    IamUserInfo ui = account.getUserInfo();
    return new DefaultAccountUpdater<String, GivenNameReplacedEvent>(account,
        ACCOUNT_REPLACE_GIVEN_NAME, ui::getGivenName, ui::setGivenName, givenName,
        buildGivenNameReplacedEvent);
  }

  public AccountUpdater familyName(String familyName) {
    final IamUserInfo ui = account.getUserInfo();
    return new DefaultAccountUpdater<String, FamilyNameReplacedEvent>(account,
        ACCOUNT_REPLACE_FAMILY_NAME, ui::getFamilyName, ui::setFamilyName, familyName,
        buildFamilyNameReplacedEvent);
  }

  public AccountUpdater picture(String newPicture) {

    final IamUserInfo ui = account.getUserInfo();
    return new DefaultAccountUpdater<String, PictureReplacedEvent>(account, ACCOUNT_REPLACE_PICTURE,
        ui::getPicture, ui::setPicture, newPicture, buildPictureReplacedEvent);

  }

  public AccountUpdater email(String email) {
    final IamUserInfo ui = account.getUserInfo();

    return new DefaultAccountUpdater<String, EmailReplacedEvent>(account, ACCOUNT_REPLACE_EMAIL,
        ui::setEmail, email, emailAddChecks, buildEmailReplacedEvent);
  }

  public AccountUpdater password(String newPassword) {

    return new DefaultAccountUpdater<String, PasswordReplacedEvent>(account,
        ACCOUNT_REPLACE_PASSWORD, encodedPasswordSetter, newPassword, encodedPasswordChecker,
        buildPasswordReplacedEvent);
  }

  public AccountUpdater username(String newUsername) {

    return new DefaultAccountUpdater<String, UsernameReplacedEvent>(account,
        ACCOUNT_REPLACE_USERNAME, account::setUsername, newUsername, usernameAddChecks,
        buildUsernameReplacedEvent);
  }

  public AccountUpdater active(boolean isActive) {

    return new DefaultAccountUpdater<Boolean, ActiveReplacedEvent>(account, ACCOUNT_REPLACE_ACTIVE,
        account::isActive, account::setActive, isActive, buildActiveReplacedEvent);
  }

}
