package it.infn.mw.iam.api.scim.new_updater;

import static it.infn.mw.iam.api.scim.model.ScimPatchOperation.ScimPatchOperationType.add;
import static java.util.Objects.nonNull;

import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.google.common.collect.Lists;

import it.infn.mw.iam.api.scim.converter.OidcIdConverter;
import it.infn.mw.iam.api.scim.model.ScimPatchOperation;
import it.infn.mw.iam.api.scim.model.ScimUser;
import it.infn.mw.iam.persistence.model.IamAccount;
import it.infn.mw.iam.persistence.model.IamOidcId;
import it.infn.mw.iam.persistence.repository.IamAccountRepository;

public class ParserImpl implements Parser {

  final PasswordEncoder encoder;

  final IamAccountRepository repo;

  final OidcIdConverter oidcIdConverter;

  public ParserImpl(PasswordEncoder encoder, IamAccountRepository repo, OidcIdConverter converter) {
    this.encoder = encoder;
    this.repo = repo;
    this.oidcIdConverter = converter;
  }

  public interface StringUpdaterFactory {
    Updater build(IamAccount a, String value);
  }

  public interface CollectionUpdaterFactory<T> {
    Updater build(IamAccount a, Collection<T> collection, IamAccountRepository repo);
  }

  private <T> void addCollectionArgUpdater(List<Updater> updaters,
      Predicate<Collection<T>> valuePredicate, Collection<T> value,
      CollectionUpdaterFactory<T> factory, IamAccount account) {

    if (valuePredicate.test(value)) {
      updaters.add(factory.build(account, value, repo));
    }
  }


  private static void addStringArgUpdater(List<Updater> updaters, Predicate<String> valuePredicate,
      Supplier<String> valueSupplier, StringUpdaterFactory factory, IamAccount account) {

    if (valuePredicate.test(valueSupplier.get())) {
      updaters.add(factory.build(account, valueSupplier.get()));
    }
  }

  private void addPasswordUpdater(List<Updater> updaters, ScimUser user, IamAccount account) {

    if (nonNull(user.getPassword())) {
      // updaters.add(Updaters.passwordUpdater(account, user.getPassword(), encoder));
    }
  }

  private List<IamOidcId> convertIds(ScimUser user) {
    return user.getIndigoUser()
      .getOidcIds()
      .stream()
      .map(i -> oidcIdConverter.fromScim(i))
      .collect(Collectors.toList());
  }



  // private void prepareAddUpdaters(List<Updater> updaters, ScimUser user, IamAccount account) {
  //
  // addStringArgUpdater(updaters, Objects::nonNull, user.getName()::getGivenName,
  // Updaters::Adders.givenNameUpdater, account);
  //
  // addStringArgUpdater(updaters, Objects::nonNull, user.getName()::getFamilyName,
  // Updaters::Adders.familyNameUpdater, account);
  //
  // if (user.hasPhotos()) {
  // addStringArgUpdater(updaters, Objects::nonNull, user.getPhotos().get(0)::getValue,
  // Updaters::Adders.pictureUpdater, account);
  // }
  //
  // addPasswordUpdater(updaters, user, account);
  //
  // addCollectionArgUpdater(updaters, Functors::collectionNotNullOrEmpty, convertIds(user),
  // Updaters::oidcIdAdder, account);
  //
  // }

  @Override
  public List<Updater> getUpdatersForRequest(IamAccount account, ScimPatchOperation<ScimUser> op) {

    List<Updater> updaters = Lists.newArrayList();

    ScimUser user = op.getValue();

    if (add.equals(op.getOp())) {
      // prepareAddUpdaters(updaters, user, account);
    }

    return updaters;
  }

}
