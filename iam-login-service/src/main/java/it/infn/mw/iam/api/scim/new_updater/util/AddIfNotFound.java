package it.infn.mw.iam.api.scim.new_updater.util;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Consumer;

public class AddIfNotFound<T> implements Consumer<Collection<T>> {

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

  public static <T> Consumer<Collection<T>> addIfNotFound(Collection<T> target) {
    return new AddIfNotFound<T>(target);
  }
}
