package com.eugenenosenko.conventional.changelog.plugin.version;

import com.eugenenosenko.conventional.changelog.plugin.exception.ChangeLogReadException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;

public final class LastVersionResolver implements VersionResolver {
  private static final Pattern TAG_REGEX =
      compile("(?!\\.)(\\d+\\.\\d+\\.\\d+)+([-.]\\w+)?([\\d+.]+)?");
  private final String firstLine;
  private String lastVersion;

  public LastVersionResolver(String firstLine) {
    this.firstLine = firstLine;
  }

  public String resolve() {
    if (lastVersion == null) {
      Matcher matcher = TAG_REGEX.matcher(firstLine);
      if (matcher.find()) {
        lastVersion = matcher.group(0);
      } else {
        throw new ChangeLogReadException(
            "First line of your changelog file need to start with "
                + " # <RELEASE_VERSION> or contain <RELEASE_VERSION>"
                + "i.e.: # [10.0.0](https://github.com/example/example/compare/9.1.9...10.0.0");
      }
    }
    return lastVersion;
  }
}
