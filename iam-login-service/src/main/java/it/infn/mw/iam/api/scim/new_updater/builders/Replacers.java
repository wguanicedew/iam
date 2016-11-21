package it.infn.mw.iam.api.scim.new_updater.builders;

import static it.infn.mw.iam.api.scim.new_updater.AccountUpdater.REPLACE_EMAIL;
import static it.infn.mw.iam.api.scim.new_updater.AccountUpdater.REPLACE_FAMILY_NAME;
import static it.infn.mw.iam.api.scim.new_updater.AccountUpdater.REPLACE_GIVEN_NAME;
import static it.infn.mw.iam.api.scim.new_updater.AccountUpdater.REPLACE_PASSWORD;
import static it.infn.mw.iam.api.scim.new_updater.AccountUpdater.REPLACE_PICTURE;

import java.util.function.Consumer;
import java.util.function.Predicate;

import org.springframework.security.crypto.password.PasswordEncoder;

import it.infn.mw.iam.api.scim.exception.ScimResourceExistsException;
import it.infn.mw.iam.api.scim.new_updater.DefaultUpdater;
import it.infn.mw.iam.api.scim.new_updater.Updater;
import it.infn.mw.iam.api.scim.new_updater.util.AccountFinder;
import it.infn.mw.iam.api.scim.new_updater.util.IdNotBoundChecker;
import it.infn.mw.iam.persistence.model.IamAccount;
import it.infn.mw.iam.persistence.model.IamUserInfo;
import it.infn.mw.iam.persistence.repository.IamAccountRepository;

public class Replacers extends BuilderSupport {

  final Predicate<String> emailAddChecks;

  final Consumer<String> encodedPasswordSetter;
  final Predicate<String> encodedPasswordChecker;

  final AccountFinder<String> findByEmail;

  private Predicate<String> buildEmailAddChecks() {
    Predicate<String> emailNotBound =
        new IdNotBoundChecker<String>(findByEmail, account, (e, a) -> {
          throw new ScimResourceExistsException("Email " + e + " already bound to another user");
        });

    Predicate<String> emailNotOwned = e -> !account.getUserInfo().getEmail().equals(e);

    return emailNotBound.and(emailNotOwned);
  }

  public Updater givenName(String givenName) {
  
    IamUserInfo ui = account.getUserInfo();
    return new DefaultUpdater<String>(REPLACE_GIVEN_NAME, ui::getGivenName, ui::setGivenName, givenName);
  }

  public Updater familyName(String familyName) {
    final IamUserInfo ui = account.getUserInfo();
    return new DefaultUpdater<String>(REPLACE_FAMILY_NAME, ui::getFamilyName, ui::setFamilyName,
        familyName);
  }

  public Updater picture(String newPicture) {
  
    final IamUserInfo ui = account.getUserInfo();
    return new DefaultUpdater<String>(REPLACE_PICTURE, ui::getPicture, ui::setPicture, newPicture);
  
  }

  public Updater email(String email) {
    final IamUserInfo ui = account.getUserInfo();
  
    return new DefaultUpdater<String>(REPLACE_EMAIL, ui::setEmail, email, emailAddChecks);
  }

  public Updater password(String newPassword) {
    return new DefaultUpdater<String>(REPLACE_PASSWORD, encodedPasswordSetter, newPassword,
        encodedPasswordChecker);
  }

  public Replacers(IamAccountRepository repo, PasswordEncoder encoder, IamAccount account) {
    super(repo, encoder, account);
    findByEmail = e -> repo.findByEmail(e);
    encodedPasswordSetter = t -> account.setPassword(encoder.encode(t));
    encodedPasswordChecker = t -> !encoder.matches(t, account.getPassword());
    emailAddChecks = buildEmailAddChecks();

  }

}
