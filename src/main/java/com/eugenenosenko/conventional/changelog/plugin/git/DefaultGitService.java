package com.eugenenosenko.conventional.changelog.plugin.git;

import com.eugenenosenko.conventional.changelog.plugin.context.VersionTag;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.DeleteTagCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.LogCommand;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.TagCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revplot.PlotWalk;
import org.eclipse.jgit.revwalk.RevCommit;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static org.eclipse.jgit.api.ResetCommand.ResetType.SOFT;

public final class DefaultGitService implements AutoCloseable, GitService {
  public final Git git;
  private final Repository repository;

  public DefaultGitService(Repository repository) {
    this.git = new Git(repository);
    this.repository = repository;
  }

  private static ObjectId getObjectId(Ref ref) {
    if (ref.getPeeledObjectId() != null) {
      return ref.getPeeledObjectId();
    }

    return ref.getObjectId();
  }

  private static String getTagShortName(Ref tagRef) {
    return tagRef.getName().replace("refs/tags/", "");
  }

  @Override
  public List<Ref> getTagList() throws GitAPIException {
    return git.tagList().call();
  }

  private List<RevCommit> fetchCommitsWithRange(ObjectId startId, ObjectId endId)
      throws IOException {
    LinkedList<RevCommit> commitsInRange = new LinkedList<>();
    PlotWalk walk = new PlotWalk(repository);
    RevCommit start = walk.parseCommit(startId);
    RevCommit end = endId != null ? walk.parseCommit(endId) : null;

    walk.markStart(start);
    RevCommit revCommit;

    while ((revCommit = walk.next()) != null) {
      if (end != null && revCommit.getId().equals(end.getId())) {
        break;
      }

      commitsInRange.addLast(revCommit);
    }

    commitsInRange.removeFirst();
    walk.close();

    return commitsInRange;
  }

  @Override
  public Map<VersionTag, List<RevCommit>> fetchCommitsFromVersion(String since)
      throws GitAPIException, IOException {
    List<Ref> tagList = getTagList();

    if (!isTheLastTag(since)) {
      for (int i = 0; i < tagList.size(); i++) {
        if (tagList.get(i).getName().contains(since)) {
          List<Ref> refs = tagList.subList(i, tagList.size());
          return fetchCommitsForTags(refs, false);
        }
      }
    }

    return Collections.emptyMap();
  }

  private boolean isTheLastTag(String sinceVersion) throws GitAPIException {
    List<Ref> tagList = getTagList();
    if (tagList.size() > 0) {
      return tagList.get(tagList.size() - 1).getName().contains(sinceVersion);
    }
    return false;
  }

  @Override
  public Map<VersionTag, List<RevCommit>> fetchCommitsForTags(
      List<Ref> tagList, boolean includeFirstTag) throws IOException {
    Map<VersionTag, List<RevCommit>> tagMap =
        new TreeMap<>(Comparator.comparing(VersionTag::getDate));
    LinkedList<Ref> queue = new LinkedList<>(tagList);

    // add additional element at the start of the queue
    // in case iterating from history start
    if (includeFirstTag) queue.addFirst(null);

    Ref fromTag = queue.pollLast();
    Ref untilTag;

    while (!queue.isEmpty()) {
      untilTag = queue.pollLast();

      ObjectId fromTagId = getObjectId(fromTag);
      ObjectId untilTagId = untilTag != null ? getObjectId(untilTag) : null;
      List<RevCommit> revCommits = fetchCommitsWithRange(fromTagId, untilTagId);
      tagMap.put(VersionTag.resolve(repository, fromTag), revCommits);

      fromTag = untilTag;
    }

    return tagMap;
  }

  @Override
  public void addFile(String filenamePattern) throws GitAPIException {
    git.add().addFilepattern(filenamePattern).call();
  }

  @Override
  public String getLastTagShortName() throws GitAPIException {
    List<Ref> tagList = git.tagList().call();
    Ref lastTag = tagList.get(getTagList().size() - 1);
    return getTagShortName(lastTag);
  }

  @Override
  public String getLastCommitMessage() throws GitAPIException {
    LogCommand log = git.log();
    log.setMaxCount(1);
    Iterable<RevCommit> call = log.call();
    RevCommit lastCommit = call.iterator().next();

    return lastCommit.getFullMessage();
  }

  @Override
  public void commitWithMessage(String message) throws GitAPIException {
    CommitCommand commit = git.commit();
    commit.setMessage(message);
    commit.call();
  }

  @Override
  public void applyTag(String tag) throws GitAPIException {
    TagCommand tagCommand = git.tag();
    tagCommand.setName(tag);
    tagCommand.call();
  }

  @Override
  public void deleteTag(String tag) throws GitAPIException {
    DeleteTagCommand deleteTagCommand = git.tagDelete();
    deleteTagCommand.setTags(tag);
    deleteTagCommand.call();
  }

  @Override
  public void softResetToRef(String ref) throws GitAPIException {
    ResetCommand resetCommand = git.reset();
    resetCommand.setRef(ref);
    resetCommand.setMode(SOFT);
    resetCommand.call();
  }

  @Override
  public void close() {
    git.close();
  }
}
