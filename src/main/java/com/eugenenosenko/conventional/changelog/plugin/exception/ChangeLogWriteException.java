package com.eugenenosenko.conventional.changelog.plugin.exception;

public class ChangeLogWriteException extends RuntimeException {
  public ChangeLogWriteException(String message) {
    super(message);
  }

  public ChangeLogWriteException(String message, Throwable cause) {
    super(message, cause);
  }
}
