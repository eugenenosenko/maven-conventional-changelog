package com.eugenenosenko.conventional.changelog.plugin.file;

public abstract class ConditionalTransformer<T> {
  protected abstract boolean test(T value);

  protected abstract T apply(T value);

  public final T transform(T value) {
    if (test(value)) {
      return apply(value);
    }
    return value;
  }
}
