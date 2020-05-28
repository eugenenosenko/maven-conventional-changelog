package com.eugenenosenko.conventional.changelog.core;

import java.util.stream.Stream;

public final class ConventionalCommit implements ICommitBody, ICommitFooter, ICommitMessage {
  private final CommitMessage commitMessage;
  private final CommitBody commitBody;
  private final CommitFooter commitFooter;

  public ConventionalCommit(
      CommitMessage commitMessage, CommitBody commitBody, CommitFooter commitFooter) {
    this.commitMessage = commitMessage;
    this.commitBody = commitBody;
    this.commitFooter = commitFooter;
  }

  public ConventionalCommit(CommitMessage commitMessage) {
    this(commitMessage, CommitBody.EMPTY, CommitFooter.EMPTY);
  }

  public ConventionalCommit(CommitMessage commitMessage, CommitFooter commitFooter) {
    this(commitMessage, CommitBody.EMPTY, commitFooter);
  }

  @Override
  public String getFooter() {
    return commitFooter.getFooter();
  }

  @Override
  public String getBody() {
    return commitBody.getBody();
  }

  @Override
  public String getRawMessage() {
    return commitMessage.getRawMessage();
  }

  @Override
  public CommitType getType() {
    return commitMessage.getType();
  }

  public String getDecoratedScope() {
    return "**" + getScope() + "**";
  }

  @Override
  public String getMessage() {
    return commitMessage.getMessage();
  }

  @Override
  public String getScope() {
    return commitMessage.getScope();
  }

  @Override
  public boolean isBreakingChange() {
    return commitMessage.isBreakingChange()
        || commitBody.isBreakingChange()
        || commitFooter.isBreakingChange();
  }

  @Override
  public String getBreakingChangeDescription() {
    return Stream.of(commitFooter, commitBody, commitMessage)
        .filter(BreakingChangeItem::isBreakingChange)
        .map(BreakingChangeItem::getBreakingChangeDescription)
        .map(string -> string.replace("BREAKING-CHANGE: ", ""))
        .map(string -> string.replace("BREAKING CHANGE: ", ""))
        .findFirst()
        .orElse("");
  }
}
