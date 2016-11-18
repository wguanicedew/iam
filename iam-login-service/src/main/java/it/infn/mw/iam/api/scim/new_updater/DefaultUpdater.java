package it.infn.mw.iam.api.scim.new_updater;

import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class DefaultUpdater<T> implements Updater {


  final T newValue;
  final Consumer<T> setter;
  final Predicate<T> applyIf;



  public DefaultUpdater(Consumer<T> consumer, T newVal, Predicate<T> predicate) {

    this.newValue = newVal;
    this.setter = consumer;
    this.applyIf = predicate;
  }

  public DefaultUpdater(Supplier<T> supplier, Consumer<T> consumer, T newVal) {
    this(consumer, newVal, notEqualsMatcher(supplier));

  }

  @Override
  public boolean update() {

    if (applyIf.test(newValue)) {
      setter.accept(newValue);
      return true;
    }

    return false;
  }

  private static <T> NotEqualsMatcher<T> notEqualsMatcher(Supplier<T> supp) {
    return new NotEqualsMatcher<T>(supp);
  }

  private static class NotEqualsMatcher<T> implements Predicate<T> {

    final Supplier<T> supplier;


    public NotEqualsMatcher(Supplier<T> supplier) {
      this.supplier = supplier;
    }

    @Override
    public boolean test(T t) {

      if (supplier.get() == null && t == null) {
        return false;
      }

      return !supplier.get().equals(t);

    }

  }
}
