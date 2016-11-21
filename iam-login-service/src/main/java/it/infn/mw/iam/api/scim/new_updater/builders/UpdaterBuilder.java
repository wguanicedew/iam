package it.infn.mw.iam.api.scim.new_updater.builders;

import it.infn.mw.iam.api.scim.new_updater.Updater;

@FunctionalInterface
public interface UpdaterBuilder<T> {
  Updater build(T value);
}
