package it.infn.mw.iam.api.scim.updater.user;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import it.infn.mw.iam.api.scim.model.ScimUser;
import it.infn.mw.iam.api.scim.updater.Updater;
import it.infn.mw.iam.persistence.model.IamAccount;

public class UserUpdaterCollection implements UpdaterCollection<IamAccount, ScimUser> {

  Collection<Updater<IamAccount, ScimUser>> updaters;

  public UserUpdaterCollection() {
    updaters = new HashSet<Updater<IamAccount, ScimUser>>();
  }

  public boolean add(IamAccount account, ScimUser updates) {

    return updaters.stream()
      .filter(updater -> updater.accept(updates))
      .map(updater -> updater.add(account, updates))
      .filter(result -> result)
      .count() > 0;
  }

  public boolean replace(IamAccount account, ScimUser updates) {

    return updaters.stream()
      .filter(updater -> updater.accept(updates))
      .map(updater -> updater.replace(account, updates))
      .filter(result -> result)
      .count() > 0;
  }

  public boolean remove(IamAccount account, ScimUser updates) {

    List<Boolean> results =
        updaters.stream().filter(updater -> updater.accept(updates)).map(updater -> {
          boolean result = updater.remove(account, updates);
          return result;
        }).filter(result -> result.booleanValue()).collect(Collectors.toList());

    return results.size() > 0;

  }

  @Override
  public void addUpdater(Updater<IamAccount, ScimUser> updater) {

    updaters.add(updater);
  }
}
