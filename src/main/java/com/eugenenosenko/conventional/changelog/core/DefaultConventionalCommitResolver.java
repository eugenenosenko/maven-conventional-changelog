package com.eugenenosenko.conventional.changelog.core;

import com.eugenenosenko.conventional.changelog.util.CommitMatcherUtil;
import org.eclipse.jgit.revwalk.RevCommit;

import java.util.ArrayList;
import java.util.List;

import static com.eugenenosenko.conventional.changelog.util.ArrayUtil.drop;
import static com.eugenenosenko.conventional.changelog.util.ArrayUtil.first;
import static com.eugenenosenko.conventional.changelog.util.ArrayUtil.last;
import static com.eugenenosenko.conventional.changelog.util.CommitMatcherUtil.COMMIT_MESSAGE_REGEX;

public final class DefaultConventionalCommitResolver implements ConventionalCommitResolver {

  @Override
  public List<ConventionalCommit> resolveConventionalCommits(Iterable<RevCommit> revCommits) {
    List<ConventionalCommit> conventionalCommits = new ArrayList<>();

    for (RevCommit revCommit : revCommits) {
      String[] commitMessageArray =
          revCommit.getFullMessage().split(CommitMatcherUtil.LINE_SEPARATOR);

      if (commitMessageArray.length >= 1
          && first(commitMessageArray).trim().matches(COMMIT_MESSAGE_REGEX.pattern())) {

        if (commitMessageArray.length == 1) {
          conventionalCommits.add(
              new ConventionalCommit(new CommitMessage(first(commitMessageArray))));
        } else if (commitMessageArray.length == 2) {
          conventionalCommits.add(
              new ConventionalCommit(
                  new CommitMessage(first(commitMessageArray)),
                  new CommitFooter(last(commitMessageArray))));
        } else {
          conventionalCommits.add(
              new ConventionalCommit(
                  new CommitMessage(first(commitMessageArray)),
                  new CommitBody(drop(1, 1, commitMessageArray)),
                  new CommitFooter(last(commitMessageArray))));
        }
      }
    }

    return conventionalCommits;
  }
}
