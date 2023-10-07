package io.github.kambaa.utils;

import java.util.List;

public class Utils {
  /**
   * Checks and returns false if given string object is not null and not equals to "".
   */
  public static boolean isEmpty(String given) {
    return !(null != given && !given.equals(""));
  }

  /**
   * Checks and returns false if given object array is not null and its size is bigger than zero.
   */
  public static boolean isEmpty(Object[] given) {
    return !(null != given && given.length > 0);
  }

  /**
   * Checks and returns false if given list is not null and its size is bigger than zero.
   */
  public static boolean isEmpty(List given) {
    return !(null != given && given.size() > 0);
  }
}
