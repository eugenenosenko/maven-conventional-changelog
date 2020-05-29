package com.eugenenosenko.conventional.changelog.util;

import com.eugenenosenko.conventional.changelog.core.CommitType;

import java.util.Arrays;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.joining;

public final class CommitMatcherUtil {
  public static final Pattern COMMIT_MESSAGE_REGEX = createRegexPattern();
  public static final String LINE_SEPARATOR = "\n\n";

  private CommitMatcherUtil() {}

  private static Pattern createRegexPattern() {
    // i.e. ^(build|test|chore|feat|fix|docs)
    String typePrefix =
        Arrays.stream(CommitType.values())
            .map(CommitType::getPrefix)
            .collect(joining("|", "^(", ")"));

    return Pattern.compile(typePrefix + "[(]?([\\w\\-]+)?[)]?(!)?:\\s(.+)");
  }
}
