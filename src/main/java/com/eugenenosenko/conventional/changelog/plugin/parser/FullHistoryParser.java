package com.eugenenosenko.conventional.changelog.plugin.parser;

import com.eugenenosenko.conventional.changelog.plugin.context.VersionTag;
import com.eugenenosenko.conventional.changelog.plugin.git.GitService;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.revwalk.RevCommit;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public final class FullHistoryParser implements ParseStrategy {

  @Override
  public Map<VersionTag, List<RevCommit>> parse(GitService gitService)
      throws GitAPIException, IOException {
    return gitService.fetchCommitsForTags(gitService.getTagList(), true);
  }
}
