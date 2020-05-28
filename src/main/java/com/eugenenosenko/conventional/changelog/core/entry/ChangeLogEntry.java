package com.eugenenosenko.conventional.changelog.core.entry;

public abstract class ChangeLogEntry {
  protected final String header;
  protected final String message;

  public ChangeLogEntry(String header, String message) {
    this.header = header;
    this.message = message;
  }

  @Override
  public String toString() {
    return header + " " + message;
  }

  public String getHeader() {
    return header;
  }

  public String getMessage() {
    return message;
  }
}
