package it.infn.mw.iam.api.scim.new_updater.builders;

import static it.infn.mw.iam.api.scim.new_updater.AccountUpdater.ADD_OIDC_ID;
import static it.infn.mw.iam.api.scim.new_updater.AccountUpdater.ADD_SAML_ID;
import static it.infn.mw.iam.api.scim.new_updater.AccountUpdater.ADD_SSH_KEY;
import static it.infn.mw.iam.api.scim.new_updater.util.AddIfNotFound.addIfNotFound;

import java.util.Collection;
import java.util.Objects;
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
import it.infn.mw.iam.persistence.model.IamSshKey;
import it.infn.mw.iam.persistence.repository.IamAccountRepository;

public class Adders extends Replacers {


  final Predicate<Collection<IamOidcId>> oidcIdAddChecks;
  final Predicate<Collection<IamSamlId>> samlIdAddChecks;
  final Predicate<Collection<IamSshKey>> sshKeyAddChecks;

  final AccountFinder<IamOidcId> findByOidcId;
  final AccountFinder<IamSamlId> findBySamlId;
  final AccountFinder<IamSshKey> findBySshKey;


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

  private Predicate<Collection<IamSshKey>> buildSshKeyAddChecks() {
    Predicate<IamSshKey> sshKeyNotBound =
        new IdNotBoundChecker<IamSshKey>(findBySshKey, account, (key, a) -> {
          throw new ScimResourceExistsException(
              "SSH key '" + key.getFingerprint() + "' already bound to another user");
        });

    Predicate<Collection<IamSshKey>> sshKeysNotBound = c -> {
      c.removeIf(Objects::isNull);
      c.stream().forEach(id -> sshKeyNotBound.test(id));
      return true;
    };

    Predicate<Collection<IamSshKey>> sshKeysNotOwned = c -> {
      return !account.getSshKeys().containsAll(c);
    };


    return sshKeysNotBound.and(sshKeysNotOwned);
  }


  public Adders(IamAccountRepository repo, PasswordEncoder encoder, IamAccount account) {
    super(repo, encoder, account);

    findByOidcId = id -> repo.findByOidcId(id.getIssuer(), id.getSubject());
    findBySamlId = id -> repo.findBySamlId(id.getIdpId(), id.getUserId());
    findBySshKey = key -> repo.findBySshKeyFingerprint(key.getFingerprint());

    oidcIdAddChecks = buildOidcIdsAddChecks();
    samlIdAddChecks = buildSamlIdsAddChecks();
    sshKeyAddChecks = buildSshKeyAddChecks();
  }

  public Updater oidcId(Collection<IamOidcId> newOidcIds) {
    final Collection<IamOidcId> oidcIds = account.getOidcIds();

    return new DefaultUpdater<Collection<IamOidcId>>(ADD_OIDC_ID, addIfNotFound(oidcIds),
        newOidcIds, oidcIdAddChecks);
  }

  public Updater samlId(Collection<IamSamlId> newSamlIds) {
    final Collection<IamSamlId> samlIds = account.getSamlIds();

    return new DefaultUpdater<Collection<IamSamlId>>(ADD_SAML_ID, addIfNotFound(samlIds),
        newSamlIds, samlIdAddChecks);
  }

  public Updater sshKey(Collection<IamSshKey> newSshKeys) {
    final Collection<IamSshKey> sshKeys = account.getSshKeys();

    return new DefaultUpdater<Collection<IamSshKey>>(ADD_SSH_KEY, addIfNotFound(sshKeys),
        newSshKeys, sshKeyAddChecks);
  }
}
