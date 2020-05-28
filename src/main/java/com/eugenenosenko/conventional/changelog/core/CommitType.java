package com.eugenenosenko.conventional.changelog.core;

public enum CommitType implements Comparable<CommitType> {
  BUILD(5, "build", "Builds"),
  TEST(6, "test", "Tests"),
  CI(9, "ci", "Continuous Integration"),
  CHORE(8, "chore", "Chores"),
  DOCS(7, "docs", "Documentation"),
  FEAT(1, "feat", "Features"),
  BUG_FIX(0, "fix", "Bug Fixes"),
  PERFORMANCE(2, "perf", "Performance Improvements"),
  STYLE(10, "style", "Styles"),
  REVERT(3, "revert", "Reverts"),
  REFACTOR(4, "refactor", "Code Refactoring");

  private final int displayPriority;
  private final String prefix;
  private final String fullName;

  CommitType(int displayPriority, String prefix, String fullName) {
    this.displayPriority = displayPriority;
    this.prefix = prefix;
    this.fullName = fullName;
  }

  public int getDisplayPriority() {
    return displayPriority;
  }

  public String getFullName() {
    return fullName;
  }

  public String getPrefix() {
    return prefix;
  }

  public static CommitType of(String value) {
    for (CommitType type : CommitType.values()) {
      if (type.prefix.equals(value)) {
        return type;
      }
    }
    throw new IllegalStateException(
        value + " commit type is not supported by " + CommitType.class.getSimpleName());
  }
}
