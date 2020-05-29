package com.eugenenosenko.conventional.changelog.plugin.exception;

public class TagListSortingException extends RuntimeException {
  public TagListSortingException(String message) {
    super(message);
  }

  public TagListSortingException(String message, Throwable cause) {
    super(message, cause);
  }
}
