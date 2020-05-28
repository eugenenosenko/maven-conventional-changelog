package com.eugenenosenko.conventional.changelog.core;

import java.util.regex.Matcher;

import static com.eugenenosenko.conventional.changelog.util.CommitMatcherUtil.COMMIT_MESSAGE_REGEX;
import static com.eugenenosenko.conventional.changelog.util.StringUtil.getNullable;

final class CommitMessage implements ICommitMessage {
  private final String rawMessage;
  private final CommitType type;
  private final String message;
  private final String scope;
  private final boolean isBreakingChange;

  CommitMessage(String rawMessage) {
    this.rawMessage = rawMessage.trim();
    ConventionalCommitMatcher matcher = new ConventionalCommitMatcher(this.rawMessage);
    this.type = matcher.getType();
    this.message = matcher.getMessage();
    this.scope = matcher.getScope();
    this.isBreakingChange = matcher.isBreakingChange();
  }

  public String getRawMessage() {
    return rawMessage;
  }

  public CommitType getType() {
    return type;
  }

  public String getMessage() {
    return message;
  }

  public String getScope() {
    return scope;
  }

  @Override
  public boolean isBreakingChange() {
    return isBreakingChange;
  }

  @Override
  public String getBreakingChangeDescription() {
    return isBreakingChange ? message : "";
  }

  private static class ConventionalCommitMatcher {
    private final Matcher matcher;

    public ConventionalCommitMatcher(String rawString) {
      this.matcher = COMMIT_MESSAGE_REGEX.matcher(rawString);
      this.matcher.find();
    }

    CommitType getType() {
      return CommitType.of(matcher.group(1));
    }

    String getScope() {
      return getNullable(matcher.group(2));
    }

    boolean isBreakingChange() {
      return matcher.group(3) != null;
    }

    String getMessage() {
      return getNullable(matcher.group(4));
    }
  }
}
