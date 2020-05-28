package com.eugenenosenko.conventional.changelog.plugin.parser;

import com.eugenenosenko.conventional.changelog.plugin.context.VersionTag;
import com.eugenenosenko.conventional.changelog.plugin.file.LogFileHandler;
import com.eugenenosenko.conventional.changelog.plugin.git.GitService;
import com.eugenenosenko.conventional.changelog.plugin.version.LastVersionResolver;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.revwalk.RevCommit;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public final class VersionTagParser implements TagParser {
  private final LogFileHandler changeLogFileHandler;
  private final GitService gitService;

  public VersionTagParser(LogFileHandler changeLogFileHandler, GitService gitService) {
    this.changeLogFileHandler = changeLogFileHandler;
    this.gitService = gitService;
  }

  @Override
  public Map<VersionTag, List<RevCommit>> parse() throws GitAPIException, IOException {
    if (changeLogFileHandler.isFileEmpty()) {
      return new FullHistoryParser().parse(gitService);
    } else {
      String lastRelease = new LastVersionResolver(changeLogFileHandler.readFirstLine()).resolve();
      return new FromTagParseStrategy(lastRelease).parse(gitService);
    }
  }
}
