package com.eugenenosenko.conventional.changelog.core;

interface BreakingChangeItem {
  boolean isBreakingChange();

  String getBreakingChangeDescription();
}
