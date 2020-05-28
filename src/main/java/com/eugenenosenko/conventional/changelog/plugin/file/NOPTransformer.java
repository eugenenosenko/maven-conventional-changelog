package com.eugenenosenko.conventional.changelog.plugin.file;

public final class NOPTransformer extends ConditionalTransformer<String> {
  public static final NOPTransformer INSTANCE = new NOPTransformer();

  @Override
  protected boolean test(String value) {
    return false;
  }

  @Override
  protected String apply(String value) {
    return value;
  }
}
