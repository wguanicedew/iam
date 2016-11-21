package it.infn.mw.iam.api.scim.new_updater.util;

import static java.util.Objects.nonNull;

import java.util.Collection;
import java.util.function.Supplier;

public class CollectionHelpers {

  public static <T> boolean notNullOrEmpty(Collection<T> c) {
    return nonNull(c) && !c.isEmpty();
  }


  public static <T> Supplier<T> nullSupplier() {
    return () -> {
      return null;
    };
  }
}
