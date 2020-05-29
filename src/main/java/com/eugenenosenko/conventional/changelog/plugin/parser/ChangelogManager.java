package com.eugenenosenko.conventional.changelog.plugin.parser;

import com.eugenenosenko.conventional.changelog.core.entry.ReleaseEntry;
import com.eugenenosenko.conventional.changelog.plugin.backup.Undoable;
import com.eugenenosenko.conventional.changelog.plugin.context.VersionTag;
import com.eugenenosenko.conventional.changelog.plugin.file.FirstParagraphTransformer;
import com.eugenenosenko.conventional.changelog.plugin.file.LogFileHandler;
import com.eugenenosenko.conventional.changelog.plugin.git.GitService;
import com.eugenenosenko.conventional.changelog.plugin.version.LastVersionResolver;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.revwalk.RevCommit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Map;

import static java.nio.file.StandardOpenOption.APPEND;

public final class ChangelogManager implements AutoCloseable, Undoable, TagParser {
  private static final Logger log = LoggerFactory.getLogger(ChangelogManager.class);
  private final LogFileHandler changelogHandler;
  private final LogFileHandler backupHandler;
  private final GitService gitService;

  private boolean backupMade = false;

  public ChangelogManager(
      GitService gitService, LogFileHandler changeLogHandler, LogFileHandler backupLogHandler) {
    this.gitService = gitService;
    this.changelogHandler = changeLogHandler;
    this.backupHandler = backupLogHandler;
  }

  @Override
  public void close() throws Exception {
    backupHandler.deleteFile();
  }

  @Override
  public void undo() {
    throw new UnsupportedOperationException("Undo not supported at this point");
  }

  @Override
  public Map<VersionTag, List<RevCommit>> parse(int releaseCount)
      throws GitAPIException, IOException {
    if (changelogHandler.isFileEmpty() && releaseCount == -1) {
      log.info("Couldn't find {} . Will try to create one", changelogHandler.getFile());
      return new FullHistoryParser().parse(gitService);
    } else if (releaseCount >= 0) {
      log.info("Fetching changelog for last {} releases", releaseCount + 1);
      return new NTagsParser(releaseCount).parse(gitService);
    } else {
      String lastRelease = new LastVersionResolver(changelogHandler.readFirstLine()).resolve();

      log.info("Changelog file already exists. Last recorded release: {}", lastRelease);
      Map<VersionTag, List<RevCommit>> parse = new FromLastTagParser(lastRelease).parse(gitService);

      log.info("Copying previous changelog file contents to a backup file.");
      makeACopyOfChangelog();

      changelogHandler.clearFileContents();
      backupMade = true;
      return parse;
    }
  }

  private void makeACopyOfChangelog() throws IOException {
    if (!backupHandler.exists()) {
      Files.createFile(backupHandler.getFile());
    }

    backupHandler.writeFrom(changelogHandler.getFile(), StandardOpenOption.WRITE);
  }

  public void writeEntriesToChangelog(List<ReleaseEntry> entries) throws IOException {
    if (!changelogHandler.exists()) {

      Files.createFile(changelogHandler.getFile());
    }

    for (ReleaseEntry entry : entries) {
      changelogHandler.appendStringToFile(entry.toString().concat("\n"));
    }

    if (backupMade) {
      Path backFile = backupHandler.getFile();
      changelogHandler.writeFrom(backFile, APPEND, new FirstParagraphTransformer());
    }
  }
}
