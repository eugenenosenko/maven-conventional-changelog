package com.eugenenosenko.conventional.changelog.plugin.context;

import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public final class VersionTag {
  private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
  private final String version;
  private final Date date;

  public VersionTag(RevCommit commit, String tagShortName) {
    date = new Date(commit.getCommitTime() * 1000L);
    version = tagShortName;
  }

  public static VersionTag resolve(Repository repository, Ref currentTag) throws IOException {
    RevCommit commit = repository.parseCommit(getObjectId(currentTag));
    return new VersionTag(commit, getTagShortName(currentTag));
  }

  private static ObjectId getObjectId(Ref reference) {
    if (reference.getPeeledObjectId() != null) {
      return reference.getPeeledObjectId();
    }

    return reference.getObjectId();
  }

  private static String getTagShortName(Ref tagRef) {
    return tagRef.getName().replace("refs/tags/", "").replace("v", "");
  }

  @Override
  public String toString() {
    return "VersionTag{" + "version='" + version + '\'' + ", date=" + date + '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    VersionTag that = (VersionTag) o;
    return Objects.equals(version, that.version) && Objects.equals(date, that.date);
  }

  @Override
  public int hashCode() {
    return Objects.hash(version, date);
  }

  public String getVersion() {
    return version;
  }

  public Date getDate() {
    return date;
  }

  public String getDecoratedVersionTag() {
    return version + " (" + DATE_FORMAT.format(date) + ")";
  }
}
