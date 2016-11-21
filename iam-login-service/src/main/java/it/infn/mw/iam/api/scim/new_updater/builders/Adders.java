package it.infn.mw.iam.api.scim.new_updater.builders;

import static it.infn.mw.iam.api.scim.new_updater.AccountUpdater.addEmail;
import static it.infn.mw.iam.api.scim.new_updater.AccountUpdater.addFamilyName;
import static it.infn.mw.iam.api.scim.new_updater.AccountUpdater.addGivenName;
import static it.infn.mw.iam.api.scim.new_updater.AccountUpdater.addOidcId;
import static it.infn.mw.iam.api.scim.new_updater.AccountUpdater.addPassword;
import static it.infn.mw.iam.api.scim.new_updater.AccountUpdater.addPicture;
import static it.infn.mw.iam.api.scim.new_updater.AccountUpdater.addSamlId;
import static it.infn.mw.iam.api.scim.new_updater.util.AddIfNotFound.addIfNotFound;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.springframework.security.crypto.password.PasswordEncoder;

import it.infn.mw.iam.api.scim.exception.ScimResourceExistsException;
import it.infn.mw.iam.api.scim.new_updater.DefaultUpdater;
import it.infn.mw.iam.api.scim.new_updater.Updater;
import it.infn.mw.iam.api.scim.new_updater.util.AccountFinder;
import it.infn.mw.iam.api.scim.new_updater.util.IdNotBoundChecker;
import it.infn.mw.iam.persistence.model.IamAccount;
import it.infn.mw.iam.persistence.model.IamOidcId;
import it.infn.mw.iam.persistence.model.IamSamlId;
import it.infn.mw.iam.persistence.model.IamUserInfo;
import it.infn.mw.iam.persistence.repository.IamAccountRepository;

public class Adders extends BuilderSupport {

  final Predicate<String> emailAddChecks;
  final Predicate<Collection<IamOidcId>> oidcIdAddChecks;
  final Predicate<Collection<IamSamlId>> samlIdAddChecks;

  final Consumer<String> encodedPasswordSetter;
  final Predicate<String> encodedPasswordChecker;

  final AccountFinder<IamOidcId> findByOidcId;
  final AccountFinder<IamSamlId> findBySamlId;
  final AccountFinder<String> findByEmail;


  private Predicate<Collection<IamOidcId>> buildOidcIdsAddChecks() {

    Predicate<IamOidcId> oidcIdNotBound =
        new IdNotBoundChecker<IamOidcId>(findByOidcId, account, (id, a) -> {
          throw new ScimResourceExistsException(
              "OpenID connect account " + id + " already bound to another user");
        });

    Predicate<Collection<IamOidcId>> oidcIdsNotBound = c -> {
      c.removeIf(Objects::isNull);
      c.stream().forEach(id -> oidcIdNotBound.test(id));
      return true;
    };

    Predicate<Collection<IamOidcId>> oidcIdsNotOwned = c -> {
      return !account.getOidcIds().containsAll(c);
    };

    return oidcIdsNotBound.and(oidcIdsNotOwned);

  }

  private Predicate<Collection<IamSamlId>> buildSamlIdsAddChecks() {
    Predicate<IamSamlId> samlIdNotBound =
        new IdNotBoundChecker<IamSamlId>(findBySamlId, account, (id, a) -> {
          throw new ScimResourceExistsException(
              "SAML account " + id + " already bound to another user");
        });

    Predicate<Collection<IamSamlId>> samlIdsNotBound = c -> {
      c.removeIf(Objects::isNull);
      c.stream().forEach(id -> samlIdNotBound.test(id));
      return true;
    };

    Predicate<Collection<IamSamlId>> samlIdsNotOwned = c -> {
      return !account.getSamlIds().containsAll(c);
    };

    return samlIdsNotBound.and(samlIdsNotOwned);

  }

  private Predicate<String> buildEmailAddChecks() {
    Predicate<String> emailNotBound =
        new IdNotBoundChecker<String>(findByEmail, account, (e, a) -> {
          throw new ScimResourceExistsException("Email " + e + " already bound to another user");
        });

    Predicate<String> emailNotOwned = e -> !account.getUserInfo().getEmail().equals(e);

    return emailNotBound.and(emailNotOwned);
  }


  public Adders(IamAccountRepository repo, PasswordEncoder encoder, IamAccount account) {
    super(repo, encoder, account);

    findByOidcId = id -> repo.findByOidcId(id.getIssuer(), id.getSubject());
    findBySamlId = id -> repo.findBySamlId(id.getIdpId(), id.getUserId());
    findByEmail = e -> repo.findByEmail(e);

    encodedPasswordSetter = t -> account.setPassword(encoder.encode(t));
    encodedPasswordChecker = t -> !encoder.matches(t, account.getPassword());

    oidcIdAddChecks = buildOidcIdsAddChecks();
    samlIdAddChecks = buildSamlIdsAddChecks();
    emailAddChecks = buildEmailAddChecks();

  }

  public Updater givenName(String givenName) {

    IamUserInfo ui = account.getUserInfo();
    return new DefaultUpdater<String>(addGivenName, ui::getGivenName, ui::setGivenName, givenName);
  }

  public Updater familyName(String familyName) {
    final IamUserInfo ui = account.getUserInfo();
    return new DefaultUpdater<String>(addFamilyName, ui::getFamilyName, ui::setFamilyName,
        familyName);
  }

  public Updater picture(String newPicture) {

    final IamUserInfo ui = account.getUserInfo();
    return new DefaultUpdater<String>(addPicture, ui::getPicture, ui::setPicture, newPicture);

  }

  public Updater email(String email) {
    final IamUserInfo ui = account.getUserInfo();

    return new DefaultUpdater<String>(addEmail, ui::setEmail, email, emailAddChecks);
  }

  public Updater password(String newPassword) {
    return new DefaultUpdater<String>(addPassword, encodedPasswordSetter, newPassword,
        encodedPasswordChecker);
  }

  public Updater oidcId(Collection<IamOidcId> newOidcIds) {
    final Collection<IamOidcId> oidcIds = account.getOidcIds();

    return new DefaultUpdater<Collection<IamOidcId>>(addOidcId, addIfNotFound(oidcIds), newOidcIds,
        oidcIdAddChecks);
  }

  public Updater samlId(Collection<IamSamlId> newSamlIds) {
    final Collection<IamSamlId> samlIds = account.getSamlIds();

    return new DefaultUpdater<Collection<IamSamlId>>(addSamlId, addIfNotFound(samlIds), newSamlIds,
        samlIdAddChecks);
  }
}
