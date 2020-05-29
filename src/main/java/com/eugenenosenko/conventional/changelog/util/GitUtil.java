package com.eugenenosenko.conventional.changelog.util;

import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;

public final class GitUtil {
  private GitUtil() {}

  public static ObjectId getObjectId(Ref ref) {
    if (ref.getPeeledObjectId() != null) {
      return ref.getPeeledObjectId();
    }

    return ref.getObjectId();
  }

  public static String getTagShortName(Ref tagRef) {
    return tagRef.getName().replace("refs/tags/", "");
  }
}
