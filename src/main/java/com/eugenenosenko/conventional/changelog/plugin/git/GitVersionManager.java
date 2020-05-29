package com.eugenenosenko.conventional.changelog.plugin.git;

import com.eugenenosenko.conventional.changelog.plugin.backup.Undoable;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static com.eugenenosenko.conventional.changelog.util.GitUtil.getTagShortName;

public final class GitVersionManager implements Undoable {
  private static final Logger log = LoggerFactory.getLogger(GitVersionManager.class);
  private final GitService gitService;

  // TODO: add memento / backup for rollback
  public GitVersionManager(GitService gitService) {
    this.gitService = gitService;
  }

  public void amendLastCommitAndAddChangelog(String changelogFile)
      throws GitAPIException, IOException {
    boolean shouldAmendTag = false;

    Ref lastTagRef = gitService.getLastTag();
    String lastTagName = getTagShortName(lastTagRef);
    RevCommit lastCommit = gitService.getLastCommit();

    if (gitService.getCommitForRef(lastTagRef).equals(lastCommit)) {
      log.info("Deleting previous tag {}", lastTagName);
      gitService.deleteTag(lastTagName);
      shouldAmendTag = true;
    }

    log.info("Amending last commit...");
    gitService.softResetToRef("HEAD~1");
    gitService.addFile(changelogFile);
    log.info("Staged changelog file {} ", changelogFile);

    gitService.commitWithMessage(lastCommit.getFullMessage());
    log.info("Reapplied last commit");

    if (shouldAmendTag) {
      gitService.applyTag(lastTagName);
      log.info("Reapplied last tag");
    }
  }

  @Override
  public void undo() {
    throw new UnsupportedOperationException("Undo operation is not supported yet");
  }
}
