package it.infn.mw.iam.api.scim.updater.user;

import it.infn.mw.iam.api.scim.updater.Updater;

public interface UpdaterCollection<T, U> {

  public void addUpdater(Updater<T, U> updater);

  public boolean add(T target, U updates);

  public boolean replace(T target, U updates);

  public boolean remove(T target, U updates);

}
