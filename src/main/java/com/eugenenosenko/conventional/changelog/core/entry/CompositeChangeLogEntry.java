package com.eugenenosenko.conventional.changelog.core.entry;

import java.util.List;

public interface CompositeChangeLogEntry {
  List<ChangeLogEntry> getChildren();
}
