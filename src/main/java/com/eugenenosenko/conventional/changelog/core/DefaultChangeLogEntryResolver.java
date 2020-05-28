package com.eugenenosenko.conventional.changelog.core;

import com.eugenenosenko.conventional.changelog.core.entry.ListItemEntry;
import com.eugenenosenko.conventional.changelog.core.entry.ReleaseEntry;
import com.eugenenosenko.conventional.changelog.core.entry.SummaryEntry;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static java.util.Comparator.comparingInt;
import static java.util.stream.Collectors.groupingBy;

public final class DefaultChangeLogEntryResolver implements ChangeLogEntryResolver {

  @Override
  public ReleaseEntry.Builder resolveChangeLogEntry(List<ConventionalCommit> conventionalCommits) {
    ReleaseEntry.Builder releaseEntryBuilder = new ReleaseEntry.Builder();
    SummaryEntry breakingChanges = new SummaryEntry("BREAKING CHANGES");

    Map<CommitType, Set<ConventionalCommit>> sortedMap = groupByCommitType(conventionalCommits);

    for (CommitType type : sortedMap.keySet()) {
      SummaryEntry summaryEntry = new SummaryEntry(type.getFullName());
      Set<ConventionalCommit> commits = sortedMap.get(type);

      for (ConventionalCommit commit : commits) {
        summaryEntry.add(createListItem(commit));

        if (commit.isBreakingChange()) {
          breakingChanges.add(createBreakingChangeListItem(commit));
        }
      }

      releaseEntryBuilder.addChild(summaryEntry);
    }

    return breakingChanges.isEmpty()
        ? releaseEntryBuilder
        : releaseEntryBuilder.addChild(breakingChanges);
  }

  private static Map<CommitType, Set<ConventionalCommit>> groupByCommitType(
      List<ConventionalCommit> conventionalCommits) {
    Map<CommitType, Set<ConventionalCommit>> groupByType =
        conventionalCommits.stream()
            .collect(groupingBy(ConventionalCommit::getType, Collectors.toSet()));

    TreeMap<CommitType, Set<ConventionalCommit>> sortedMap =
        new TreeMap<>(comparingInt(CommitType::getDisplayPriority));
    sortedMap.putAll(groupByType);

    return sortedMap;
  }

  private static ListItemEntry createListItem(ConventionalCommit commit) {
    return createListItemEntry(commit, commit.getScope(), commit.getMessage());
  }

  private static ListItemEntry createBreakingChangeListItem(ConventionalCommit commit) {
    return createListItemEntry(commit, commit.getScope(), commit.getBreakingChangeDescription());
  }

  private static ListItemEntry createListItemEntry(
      ConventionalCommit commit, String scope, String message) {
    return new ListItemEntry(
        !scope.isEmpty() ? commit.getDecoratedScope() + " " + message : message);
  }
}
