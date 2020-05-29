package com.eugenenosenko.conventional.changelog.plugin.context;

import com.eugenenosenko.conventional.changelog.core.ChangeLogEntryResolver;
import com.eugenenosenko.conventional.changelog.core.ConventionalCommit;
import com.eugenenosenko.conventional.changelog.core.ConventionalCommitResolver;
import com.eugenenosenko.conventional.changelog.core.DefaultChangeLogEntryResolver;
import com.eugenenosenko.conventional.changelog.core.DefaultConventionalCommitResolver;
import com.eugenenosenko.conventional.changelog.core.entry.ReleaseEntry;
import org.eclipse.jgit.revwalk.RevCommit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class DefaultReleaseEntryResolver implements ReleaseEntryResolver {
  private static final Logger log = LoggerFactory.getLogger(DefaultReleaseEntryResolver.class);

  private final ChangeLogEntryResolver entryResolver;
  private final ConventionalCommitResolver commitResolver;

  public DefaultReleaseEntryResolver(
      ChangeLogEntryResolver entryResolver, ConventionalCommitResolver commitResolver) {
    this.entryResolver = entryResolver;
    this.commitResolver = commitResolver;
  }

  public DefaultReleaseEntryResolver() {
    this(new DefaultChangeLogEntryResolver(), new DefaultConventionalCommitResolver());
  }

  @Override
  public List<ReleaseEntry> resolve(Map<VersionTag, List<RevCommit>> versionTagMap) {
    LinkedList<ReleaseEntry.Builder> entries = new LinkedList<>();

    for (VersionTag tag : versionTagMap.keySet()) {
      List<ConventionalCommit> commitList =
          commitResolver.resolveConventionalCommits(versionTagMap.get(tag));
      ReleaseEntry.Builder builder = entryResolver.resolveChangeLogEntry(commitList);
      ReleaseEntry.Builder release =
          builder.addHeader("##").addMessage(tag.getDecoratedVersionTag());
      entries.addFirst(release);
    }

    if (entries.size() > 0) entries.peekFirst().addHeader("#");
    return entries.stream().map(ReleaseEntry.Builder::build).collect(Collectors.toList());
  }
}
