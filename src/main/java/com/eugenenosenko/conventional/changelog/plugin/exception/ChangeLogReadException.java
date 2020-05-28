package com.eugenenosenko.conventional.changelog.plugin.exception;

public class ChangeLogReadException extends RuntimeException {
  public ChangeLogReadException(String message) {
    super(message);
  }

  public ChangeLogReadException(String message, Throwable cause) {
    super(message, cause);
  }
}
