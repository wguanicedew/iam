package it.infn.mw.iam.api.scim.new_updater;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;

import it.infn.mw.iam.api.scim.exception.ScimResourceExistsException;
import it.infn.mw.iam.persistence.model.IamAccount;
import it.infn.mw.iam.persistence.model.IamOidcId;
import it.infn.mw.iam.persistence.model.IamUserInfo;
import it.infn.mw.iam.persistence.repository.IamAccountRepository;

public class Updaters {

  public static Adders adders(IamAccountRepository repo, PasswordEncoder encoder,
      IamAccount account) {
    return new Adders(repo, encoder, account);
  }

  public static class Adders {

    final IamAccountRepository repo;
    final PasswordEncoder encoder;
    final IamAccount account;

    public Adders(IamAccountRepository repo, PasswordEncoder encoder, IamAccount account) {
      this.repo = repo;
      this.encoder = encoder;
      this.account = account;
    }

    public Updater givenName(String givenName) {

      IamUserInfo ui = account.getUserInfo();
      return new DefaultUpdater<String>(ui::getGivenName, ui::setGivenName, givenName);
    }

    public Updater familyName(String familyName) {
      IamUserInfo ui = account.getUserInfo();
      return new DefaultUpdater<String>(ui::getFamilyName, ui::setFamilyName, familyName);
    }

    public Updater picture(String newPicture) {

      IamUserInfo ui = account.getUserInfo();
      return new DefaultUpdater<String>(ui::getPicture, ui::setPicture, newPicture);

    }

    public Updater email(String email) {
      IamUserInfo ui = account.getUserInfo();

      return new DefaultUpdater<String>(ui::setEmail, email, e -> {

        repo.findByEmailWithDifferentUUID(e, account.getUuid()).ifPresent(acc -> {
          throw new ScimResourceExistsException("Email already bound to another user");
        });

        return !e.equals(account.getUserInfo().getEmail());

      });
    }

    public Updater password(String newPassword) {
      return new DefaultUpdater<String>(t -> account.setPassword(encoder.encode(newPassword)),
          newPassword, t -> !encoder.matches(t, account.getPassword()));
    }

    public Updater oidcId(Collection<IamOidcId> newOidcIds) {
      Collection<IamOidcId> oidcIds = account.getOidcIds();

      return new DefaultUpdater<Collection<IamOidcId>>(addIfNotFound(oidcIds), newOidcIds,
          oidcIdAddChecks());
    }

    private Predicate<Collection<IamOidcId>> oidcIdAddChecks() {
      return new OidcIdNotBoundOrAlreadyOwnedChecker();
    }

    class OidcIdNotBoundOrAlreadyOwnedChecker implements Predicate<Collection<IamOidcId>> {

      public void checkIdNotBoundToDifferentAccount(IamOidcId id) {

        repo.findByOidcId(id.getIssuer(), id.getSubject()).ifPresent(other -> {
          if (!other.equals(account)) {
            throw new ScimResourceExistsException("OpenID account already bound to another user");
          }
        });
      }


      @Override
      public boolean test(Collection<IamOidcId> l) {
        checkNotNull(l);

        // Filter nulls and duplicates
        Set<IamOidcId> filteredIds =
            l.stream().filter(Objects::nonNull).collect(Collectors.toSet());

        filteredIds.forEach(this::checkIdNotBoundToDifferentAccount);

        return !account.getOidcIds().containsAll(filteredIds);
      }

    }
  }

  public static <T> Consumer<Collection<T>> addIfNotFound(Collection<T> target) {
    return new AddIfNotFound<>(target);
  }

  static class AddIfNotFound<T> implements Consumer<Collection<T>> {

    final Collection<T> target;

    public AddIfNotFound(Collection<T> target) {
      this.target = target;
    }

    @Override
    public void accept(Collection<T> t) {
      checkNotNull(t);
      t.stream().filter(Objects::nonNull).forEach(i -> {
        if (!target.contains(i)) {
          target.add(i);
        }
      });

    }
  }

}
