package com.eugenenosenko.conventional.changelog.plugin.git;

import com.eugenenosenko.conventional.changelog.plugin.context.VersionTag;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface GitService {

  List<Ref> getTagList() throws GitAPIException;

  RevCommit getCommitForRef(Ref ref) throws IOException;

  Map<VersionTag, List<RevCommit>> fetchCommitsFromVersion(String sinceVersion)
      throws GitAPIException, IOException;

  Map<VersionTag, List<RevCommit>> fetchCommitsForTags(List<Ref> tagList, boolean includeFirstTag)
      throws IOException;

  void addFile(String changelogFile) throws GitAPIException;

  Ref getLastTag() throws GitAPIException;

  RevCommit getLastCommit() throws GitAPIException;

  void commitWithMessage(String message) throws GitAPIException;

  void applyTag(String tag) throws GitAPIException;

  void deleteTag(String tag) throws GitAPIException;

  void softResetToRef(String ref) throws GitAPIException;
}
