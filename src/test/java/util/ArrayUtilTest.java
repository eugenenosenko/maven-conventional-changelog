package util;

import com.eugenenosenko.conventional.changelog.util.ArrayUtil;
import org.junit.Assert;
import org.junit.Test;

public class ArrayUtilTest {

  @Test
  public void first_should_return_first_element() {
    String[] strings = {"a", "b", "c"};

    Assert.assertEquals(ArrayUtil.first(strings), "a");
  }

  @Test
  public void last_should_return_last_element() {
    String[] strings = {"a", "b", "c"};

    Assert.assertEquals(ArrayUtil.last(strings), "c");
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void last_should_throw_index_out_of_bound_exception_when_array_is_empty() {
    String[] strings = {};

    Assert.assertEquals(ArrayUtil.last(strings), "c");
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void first_should_throw_index_out_of_bound_exception_when_array_is_empty() {
    String[] strings = {};

    Assert.assertEquals(ArrayUtil.first(strings), "c");
  }

  @Test
  public void drop_from_start_should_correctly_drop_n_amount_of_items() {
    String[] strings = {"a", "b", "c"};

    Assert.assertArrayEquals(new String[] {"c"}, ArrayUtil.drop(2, strings));
  }

  @Test
  public void dropping_all_items_from_start_should_correctly_return_empty_list() {
    String[] strings = {"a", "b", "c"};

    Assert.assertArrayEquals(new String[0], ArrayUtil.drop(3, strings));
  }

  @Test
  public void dropping_from_start_and_end_should_correctly_drop_items_on_both_ends() {
    String[] strings = {"a", "b", "c"};

    Assert.assertArrayEquals(new String[] {"b"}, ArrayUtil.drop(1, 1, strings));
  }

  @Test
  public void dropping_all_items_from_both_ends_should_return_empty_array() {
    String[] strings = {"a", "b", "c"};

    Assert.assertArrayEquals(new String[0], ArrayUtil.drop(1, 2, strings));
  }

  @Test(expected = IllegalArgumentException.class)
  public void dropping_more_items_than_arrays_contains_should_throw_an_exception() {
    String[] strings = {"a", "b", "c"};

    Assert.assertArrayEquals(new String[0], ArrayUtil.drop(0, 5, strings));
  }
}
