package com.eugenenosenko.conventional.changelog.core;

public interface ICommitMessage extends BreakingChangeItem {
  String getRawMessage();

  CommitType getType();

  String getMessage();

  String getScope();
}
