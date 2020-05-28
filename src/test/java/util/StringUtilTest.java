package util;

import com.eugenenosenko.conventional.changelog.util.StringUtil;
import org.junit.Assert;
import org.junit.Test;

public class StringUtilTest {

  @Test
  public void should_get_empty_string_for_null_value() {
    String nullable = null;

    Assert.assertEquals(StringUtil.getNullable(nullable), "");
  }

  @Test
  public void should_get_empty_string_for_blank_value() {
    String blank = "           ";

    Assert.assertEquals(StringUtil.getNullable(blank), "");
  }

  @Test
  public void should_original_string_when_string_is_not_blank_or_null() {
    String nullable = "null";

    Assert.assertEquals(StringUtil.getNullable(nullable), "null");
  }
}