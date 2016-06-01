package it.infn.mw.iam.api.scim.model;

public class ScimPatchOperation<T> {

  public static enum PatchOperationType {
    add,
    remove,
    replace
  }

  private final PatchOperationType op;
  private final Path path;
  private final T value;

  private ScimPatchOperation(Builder<T> builder) {

    this.op = builder.op;
    this.path = builder.path;
    this.value = builder.value;
  }

  public PatchOperationType getOp() {

    return op;
  }

  public T getValue() {

    return value;
  }

  public Path getPath() {

    return path;
  }

  public static class Builder<T> {

    PatchOperationType op;
    Path path;
    T value;

    public Builder() {
    }

    public Builder<T> op(PatchOperationType op) {

      this.op = op;
      return this;
    }

    public Builder<T> path(Path path) {

      this.path = path;
      return this;
    }

    public Builder<T> value(T val) {

      this.value = val;
      return this;
    }

    public ScimPatchOperation<T> build() {

      return new ScimPatchOperation<>(this);
    }
  }
}
