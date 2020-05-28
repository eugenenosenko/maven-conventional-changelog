package com.eugenenosenko.conventional.changelog.plugin.git;

import com.eugenenosenko.conventional.changelog.plugin.backup.Undoable;
import org.eclipse.jgit.api.errors.GitAPIException;

public final class GitVersionManager implements Undoable {
  private final GitService gitService;

  // TODO: add memento for rollback
  public GitVersionManager(GitService gitService) {
    this.gitService = gitService;
  }

  public void amendLastCommitAndWithChangelog(String changelogFile) throws GitAPIException {
    String lastReleaseTag = gitService.getLastTagShortName();
    String lastCommitMessage = gitService.getLastCommitMessage();

    gitService.deleteTag(lastReleaseTag);
    gitService.softResetToRef("HEAD~1");
    gitService.addFile(changelogFile);
    gitService.commitWithMessage(lastCommitMessage);
    gitService.applyTag(lastReleaseTag);
  }

  @Override
  public void undo() {
    throw new UnsupportedOperationException("Undo operation is not supported yet");
  }
}
