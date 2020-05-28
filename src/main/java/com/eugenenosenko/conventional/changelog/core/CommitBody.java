package com.eugenenosenko.conventional.changelog.core;

import edu.emory.mathcs.backport.java.util.Arrays;

import java.util.List;
import java.util.function.Predicate;

final class CommitBody implements ICommitBody {
  private final Predicate<String> breakingChangePredicate = CommitBody::doesContainBreakingChange;
  public static final CommitBody EMPTY = new CommitBody(new String[0]);
  private final boolean isBreakingChange;
  private final List<String> body;

  public CommitBody(String[] rawBody) {
    this.body = Arrays.asList(rawBody);
    this.isBreakingChange = body.stream().anyMatch(breakingChangePredicate);
  }

  @Override
  public boolean isBreakingChange() {
    return isBreakingChange;
  }

  @Override
  public String getBreakingChangeDescription() {
    return getBody();
  }

  @Override
  public String getBody() {
    return isBreakingChange
        ? getBreakingChangeText()
        : ""; // body is not required unless breaking change
  }

  private static boolean doesContainBreakingChange(String string) {
    return string.contains("BREAKING-CHANGE:") || string.contains("BREAKING CHANGE");
  }

  private String getBreakingChangeText() {
    for (String line : body) {
      if (breakingChangePredicate.test(line)) {
        return line;
      }
    }
    return "";
  }
}
