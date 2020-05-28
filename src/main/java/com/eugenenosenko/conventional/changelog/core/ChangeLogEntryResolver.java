package com.eugenenosenko.conventional.changelog.core;

import com.eugenenosenko.conventional.changelog.core.entry.ReleaseEntry;

import java.util.List;

public interface ChangeLogEntryResolver {
  ReleaseEntry.Builder resolveChangeLogEntry(List<ConventionalCommit> conventionalCommit);
}
