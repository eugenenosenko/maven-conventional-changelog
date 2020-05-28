package com.eugenenosenko.conventional.changelog.util;

public final class StringUtil {
  private StringUtil() {}

  public static String getNullable(String nullable) {
    if (nullable == null || nullable.trim().isEmpty()) {
      return "";
    }
    return nullable;
  }
}
