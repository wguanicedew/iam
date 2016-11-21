package it.infn.mw.iam.api.scim.new_updater;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.google.common.collect.Lists;

import it.infn.mw.iam.api.scim.converter.OidcIdConverter;
import it.infn.mw.iam.api.scim.converter.SamlIdConverter;
import it.infn.mw.iam.api.scim.converter.SshKeyConverter;
import it.infn.mw.iam.api.scim.model.ScimOidcId;
import it.infn.mw.iam.api.scim.model.ScimPatchOperation;
import it.infn.mw.iam.api.scim.model.ScimSamlId;
import it.infn.mw.iam.api.scim.model.ScimSshKey;
import it.infn.mw.iam.api.scim.model.ScimUser;
import it.infn.mw.iam.api.scim.new_updater.builders.Adders;
import it.infn.mw.iam.api.scim.new_updater.builders.Removers;
import it.infn.mw.iam.api.scim.new_updater.builders.Replacers;
import it.infn.mw.iam.api.scim.new_updater.builders.UpdaterBuilder;
import it.infn.mw.iam.api.scim.new_updater.util.CollectionHelpers;
import it.infn.mw.iam.api.scim.new_updater.util.ScimCollectionConverter;
import it.infn.mw.iam.persistence.model.IamAccount;
import it.infn.mw.iam.persistence.model.IamOidcId;
import it.infn.mw.iam.persistence.model.IamSamlId;
import it.infn.mw.iam.persistence.model.IamSshKey;
import it.infn.mw.iam.persistence.repository.IamAccountRepository;

public class DefaultAccountUpdaterFactory implements UpdaterFactory<IamAccount, ScimUser> {

  final PasswordEncoder encoder;

  final IamAccountRepository repo;

  final OidcIdConverter oidcIdConverter;
  final SamlIdConverter samlIdConverter;
  final SshKeyConverter sshKeyConverter;

  public DefaultAccountUpdaterFactory(PasswordEncoder encoder, IamAccountRepository repo,
      OidcIdConverter oidcIdConverter, SamlIdConverter samlIdConverter,
      SshKeyConverter sshKeyConverter) {
    this.encoder = encoder;
    this.repo = repo;
    this.oidcIdConverter = oidcIdConverter;
    this.samlIdConverter = samlIdConverter;
    this.sshKeyConverter = sshKeyConverter;
  }

  private ScimCollectionConverter<IamSshKey, ScimSshKey> sshKeyConverter(ScimUser user) {

    return new ScimCollectionConverter<IamSshKey, ScimSshKey>(user.getIndigoUser()::getSshKeys,
        sshKeyConverter::fromScim);
  }

  private ScimCollectionConverter<IamOidcId, ScimOidcId> oidcIdConverter(ScimUser user) {
    return new ScimCollectionConverter<IamOidcId, ScimOidcId>(user.getIndigoUser()::getOidcIds,
        oidcIdConverter::fromScim);
  }

  private ScimCollectionConverter<IamSamlId, ScimSamlId> samlIdConverter(ScimUser user) {
    return new ScimCollectionConverter<IamSamlId, ScimSamlId>(user.getIndigoUser()::getSamlIds,
        samlIdConverter::fromScim);
  }


  private static <T> Updater buildUpdater(UpdaterBuilder<T> factory, Supplier<T> valueSupplier) {
    return factory.build(valueSupplier.get());
  }

  private static <T> void addUpdater(List<Updater> updaters, Predicate<T> valuePredicate,
      Supplier<T> valueSupplier, UpdaterBuilder<T> factory) {

    if (valuePredicate.test(valueSupplier.get())) {
      updaters.add(buildUpdater(factory, valueSupplier));
    }
  }

  private void prepareAdders(List<Updater> updaters, ScimUser user, IamAccount account) {

    Adders add = Updaters.adders(repo, encoder, account);

    addUpdater(updaters, Objects::nonNull, user.getName()::getGivenName, add::givenName);
    addUpdater(updaters, Objects::nonNull, user.getName()::getFamilyName, add::familyName);
    addUpdater(updaters, Objects::nonNull, user::getPassword, add::password);

    if (user.hasPhotos()) {
      addUpdater(updaters, Objects::nonNull, user.getPhotos().get(0)::getValue, add::picture);
    }

    if (user.hasOidcIds()) {
      addUpdater(updaters, CollectionHelpers::notNullOrEmpty, oidcIdConverter(user), add::oidcId);
    }

    if (user.hasSamlIds()) {
      addUpdater(updaters, CollectionHelpers::notNullOrEmpty, samlIdConverter(user), add::samlId);
    }

    if (user.hasSshKeys()) {
      addUpdater(updaters, CollectionHelpers::notNullOrEmpty, sshKeyConverter(user), add::sshKey);
    }
  }

  private void prepareRemovers(List<Updater> updaters, ScimUser user, IamAccount account) {
    Removers remove = Updaters.removers(repo, account);

    if (user.hasOidcIds()) {
      addUpdater(updaters, CollectionHelpers::notNullOrEmpty, oidcIdConverter(user),
          remove::oidcId);
    }
  }

  private void prepareReplacers(List<Updater> updaters, ScimUser user, IamAccount account) {
    Replacers replace = Updaters.replacers(repo, encoder, account);

    // TBD
  }

  @Override
  public List<Updater> getUpdatersForPatchOperation(IamAccount account,
      ScimPatchOperation<ScimUser> op) {

    final List<Updater> updaters = Lists.newArrayList();

    final ScimUser user = op.getValue();

    switch (op.getOp()) {
      case add:
        prepareAdders(updaters, user, account);
        break;

      case remove:
        prepareRemovers(updaters, user, account);
        break;

      case replace:
        prepareReplacers(updaters, user, account);
        break;

      default:
        throw new IllegalArgumentException("Unsupported operation type: " + op.getOp());
    }

    return updaters;
  }

}
