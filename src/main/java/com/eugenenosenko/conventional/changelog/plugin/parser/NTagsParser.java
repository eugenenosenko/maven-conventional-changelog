package com.eugenenosenko.conventional.changelog.plugin.parser;

import com.eugenenosenko.conventional.changelog.plugin.context.VersionTag;
import com.eugenenosenko.conventional.changelog.plugin.git.GitService;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public final class NTagsParser implements ParseStrategy {
  private final int releaseCount;

  public NTagsParser(int releaseCount) {
    this.releaseCount = releaseCount;
  }

  @Override
  public Map<VersionTag, List<RevCommit>> parse(GitService gitService)
      throws GitAPIException, IOException {
    List<Ref> tagList = gitService.getTagList();

    if (releaseCount >= tagList.size()) {
      return gitService.fetchCommitsForTags(tagList, true);
    }

    if (releaseCount == 0) {
      return gitService.fetchCommitsForTags(
          tagList.subList(tagList.size() - 2, tagList.size()), false);
    }

    return gitService.fetchCommitsForTags(
        tagList.subList(
            tagList.size() - Math.min(tagList.size(), (tagList.size() - 2 + releaseCount)),
            tagList.size()),
        false);
  }
}
