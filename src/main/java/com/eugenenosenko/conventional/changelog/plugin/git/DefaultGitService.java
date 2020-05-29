package com.eugenenosenko.conventional.changelog.plugin.git;

import com.eugenenosenko.conventional.changelog.plugin.context.VersionTag;
import com.eugenenosenko.conventional.changelog.plugin.exception.TagListSortingException;
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
import java.util.function.Function;

import static com.eugenenosenko.conventional.changelog.util.GitUtil.getObjectId;
import static org.eclipse.jgit.api.ResetCommand.ResetType.SOFT;

public final class DefaultGitService implements AutoCloseable, GitService {
  private final Repository repository;
  private final Git git;

  public DefaultGitService(Repository repository) {
    this.repository = repository;
    this.git = new Git(repository);
  }

  @Override
  public List<Ref> getTagList() throws GitAPIException {
    List<Ref> unsortedTagList = git.tagList().call();
    return sortTagListByDate(unsortedTagList);
  }

  private List<Ref> sortTagListByDate(List<Ref> unsortedTagList) {
    unsortedTagList.sort(Comparator.comparing(tagSortingFunction(repository)));
    return unsortedTagList;
  }

  @Override
  public RevCommit getCommitForRef(Ref ref) throws IOException {
    return repository.parseCommit(getObjectId(ref));
  }

  private static Function<Ref, Integer> tagSortingFunction(Repository repository) {
    return ref -> {
      try {
        return repository.parseCommit(getObjectId(ref)).getCommitTime();
      } catch (IOException e) {
        throw new TagListSortingException(
            "Failed to sort tag list. Exception occurred when "
                + "tried to parse commit from tag ref",
            e);
      }
    };
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
  public Ref getLastTag() throws GitAPIException {
    List<Ref> tagList = git.tagList().call();
    return tagList.get(getTagList().size() - 1);
  }

  @Override
  public RevCommit getLastCommit() throws GitAPIException {
    LogCommand log = git.log();
    log.setMaxCount(1);
    Iterable<RevCommit> call = log.call();

    return call.iterator().next();
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
