package io.github.kambaa.utils;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Utility Class.
 */
public class Utils {
  /**
   * Constructor.
   */
  private Utils() {
  }

  /**
   * Checks and returns false if given string object is not null and not equals to "".
   *
   * @param given given String
   * @return boolean
   */
  public static boolean isEmpty(String given) {
    return !(null != given && !given.equals(""));
  }

  /**
   * Checks and returns false if given object array is not null and its size is bigger than zero.
   *
   * @param given given array
   * @return boolean
   */
  public static boolean isEmpty(Object[] given) {
    return !(null != given && given.length > 0);
  }

  /**
   * Checks and returns false if given list is not null and its size is bigger than zero.
   *
   * @param given list
   * @return boolean
   */
  public static boolean isEmpty(List given) {
    return !(null != given && given.size() > 0);
  }

  /**
   * Checks and returns false if given list is not null and its size is bigger than zero.
   *
   * @param given list
   * @return boolean
   */
  public static boolean isEmpty(Collection given) {
    return !(null != given && given.size() > 0);
  }

  /**
   * Default splitting text(without quotes).
   * "\n------------------------ &gt;8 ------------------------\n"
   */
  public static final String DEFAULT_SEPARATOR = "\n------------------------ >8 ------------------------\n";

  /**
   * Default subject type list. Which are:
   * build, chore, ci, docs, feat, fix, perf, refactor, revert, style, test.
   */
  public static final List<String> DEFAULT_COMMIT_MESSAGE_SUBJECT_TYPES = Arrays.asList("build", "chore", "ci", "docs", "feat", "fix", "perf", "refactor", "revert", "style", "test");

  /**
   * Commit message splitting regular expression. This regex will be used for default commit message checking.
   */
  public static final String COMMIT_MESSAGE_SPLITTING_REGEX = "^(\\w*)(?:\\(([\\w\\-.]+)\\))?(!)?: (.*)(\\n[\\s\\S]*)?";

  public static boolean dummyCheckMessage(String msg) {
    return true;
  }
}
