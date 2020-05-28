package com.eugenenosenko.conventional.changelog.core.entry;

public final class ListItemEntry extends AbstractCompositeChangeLogEntry {
  public ListItemEntry(String message) {
    super("*", message);
  }
}
