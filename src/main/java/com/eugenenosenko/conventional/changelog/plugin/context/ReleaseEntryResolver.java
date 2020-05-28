package com.eugenenosenko.conventional.changelog.plugin.context;

import com.eugenenosenko.conventional.changelog.core.entry.ReleaseEntry;
import org.eclipse.jgit.revwalk.RevCommit;

import java.util.List;
import java.util.Map;

public interface ReleaseEntryResolver {
  List<ReleaseEntry> resolve(Map<VersionTag, List<RevCommit>> versionTagMap);
}
