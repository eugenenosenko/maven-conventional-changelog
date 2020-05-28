package com.eugenenosenko.conventional.changelog.util;

import java.util.Arrays;

public final class ArrayUtil {
  private ArrayUtil() {}

  public static <T> T first(T[] array) {
    return array[0];
  }

  public static <T> T last(T[] array) {
    return array[array.length - 1];
  }

  public static <T> T[] drop(int numberOfItemsToDrop, T[] array) {
    return drop(numberOfItemsToDrop, 0, array);
  }

  public static <T> T[] drop(int fromStart, int fromEnd, T[] array) {
    return Arrays.copyOfRange(array, fromStart, array.length - fromEnd);
  }
}
