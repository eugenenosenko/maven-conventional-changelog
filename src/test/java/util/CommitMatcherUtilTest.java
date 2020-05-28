package util;

import org.junit.Assert;
import org.junit.Test;

import java.util.regex.Matcher;

import static com.eugenenosenko.conventional.changelog.util.CommitMatcherUtil.COMMIT_MESSAGE_REGEX;

public class CommitMatcherUtilTest {

  @Test
  public void feature_with_scope_commit_should_properly_match_against_regex() {
    String featureCommit = "feat(test)!: added a new feature for testing";

    Matcher matcher = COMMIT_MESSAGE_REGEX.matcher(featureCommit);
    Assert.assertTrue(matcher.find());
    Assert.assertEquals(matcher.group(0), featureCommit);
    Assert.assertEquals(matcher.group(1), "feat");
    Assert.assertEquals(matcher.group(2), "test");
    Assert.assertEquals(matcher.group(3), "!");
    Assert.assertEquals(matcher.group(4), "added a new feature for testing");
  }

  @Test
  public void non_conventional_commit_should_not_match_against_regex() {
    String featureCommit = "Added a new feature for testing";

    Matcher matcher = COMMIT_MESSAGE_REGEX.matcher(featureCommit);
    Assert.assertFalse(matcher.find());
  }

  @Test
  public void feature_without_scope_commit_should_properly_match_against_regex() {
    String featureCommit = "feat: added a new feature for testing";

    Matcher matcher = COMMIT_MESSAGE_REGEX.matcher(featureCommit);
    Assert.assertTrue(matcher.find());
    Assert.assertEquals(matcher.group(0), featureCommit);
    Assert.assertEquals(matcher.group(1), "feat");
    Assert.assertNull(matcher.group(2));
    Assert.assertNull(matcher.group(3));
    Assert.assertEquals(matcher.group(4), "added a new feature for testing");
  }
}
