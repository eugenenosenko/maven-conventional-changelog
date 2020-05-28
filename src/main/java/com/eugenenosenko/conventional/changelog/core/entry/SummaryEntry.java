package com.eugenenosenko.conventional.changelog.core.entry;

public final class SummaryEntry extends AbstractCompositeChangeLogEntry {
  public SummaryEntry(String message) {
    super("###", message);
  }
}
