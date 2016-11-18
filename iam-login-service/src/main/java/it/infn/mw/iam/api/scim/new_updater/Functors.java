package it.infn.mw.iam.api.scim.new_updater;

import static java.util.Objects.nonNull;

import java.util.Collection;

public class Functors {

  public static <T> boolean collectionNotNullOrEmpty(Collection<T> c) {
    return nonNull(c) && !c.isEmpty();
  }

}
