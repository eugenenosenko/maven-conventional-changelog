package com.eugenenosenko.conventional.changelog.core;

final class CommitFooter implements ICommitFooter {
  public static final CommitFooter EMPTY = new CommitFooter("");
  private final boolean isBreakingChange;
  private final String footer;

  public CommitFooter(String footer) {
    this.isBreakingChange =
        footer.startsWith("BREAKING-CHANGE: ") || footer.startsWith("BREAKING CHANGE: ");
    this.footer =
        isBreakingChange ? footer.substring("BREAKING-CHANGE: ".length()).trim() : footer.trim();
  }

  public String getFooter() {
    return footer;
  }

  @Override
  public boolean isBreakingChange() {
    return isBreakingChange;
  }

  @Override
  public String getBreakingChangeDescription() {
    return isBreakingChange ? footer : "";
  }
}
