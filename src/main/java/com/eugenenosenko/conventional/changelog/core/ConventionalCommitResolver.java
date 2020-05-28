package com.eugenenosenko.conventional.changelog.core;

import org.eclipse.jgit.revwalk.RevCommit;

import java.util.List;

public interface ConventionalCommitResolver {
  List<ConventionalCommit> resolveConventionalCommits(Iterable<RevCommit> revCommitIterator);
}
