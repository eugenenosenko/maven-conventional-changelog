package com.eugenenosenko.conventional.changelog.plugin.file;

public final class FirstParagraphTransformer extends ConditionalTransformer<String> {
  private boolean completed = false;

  @Override
  public boolean test(String value) {
    return !completed && value.startsWith("# ");
  }

  @Override
  public String apply(String value) {
    String result = value.replaceFirst("# ", "## ");
    completed = true;
    return result;
  }
}
