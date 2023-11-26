/**
 * Test Commit Messages.
 */
public class TestCommitMessages {
  /**
   * A good working commit message.
   */
  public static final String GOOD_COMMIT_MSG = "feat: testing for the first poc\n"
                                               + "\n"

                                               + "added poc notes on the readme file";
  /**
   * A good working commit message with scope and breaking change indicator.
   */
  public static final String GOOD_COMMIT_MSG_WITH_SCOPE_AND_BREAKING_CHANGE_INDICATOR = "feat(general)!: testing for the first poc\n"
                                                                                        + "\n"
                                                                                        + "added poc notes on the readme file";

  /**
   * A bad commit message, not complying with the splitting regular expression(in the mojo).
   */
  public static final String BAD_COMMIT_MSG_NOT_COMPLIES_WITH_THE_SPLITTING_REGEX = "feat testing for the first poc";

  /**
   * A bad commit message that has unknown subject type.
   */
  public static final String BAD_COMMIT_MSG_UNKNOWN_SUBJECT_TYPE = "feast: testing for the first poc.\n"
                                                                   + "\n"
                                                                   + "added poc notes on the readme file";

  /**
   * A bad commit message that ends with period.
   */
  public static final String BAD_COMMIT_MSG_ENDING_WITH_PERIOD = "feat: testing for the first poc.\n"
                                                                 + "\n"
                                                                 + "added poc notes on the readme file";

  /**
   * A bad commit message that has empty description on subject line.
   */
  public static final String BAD_COMMIT_MSG_EMPTY_DESCRIPTION = "feat: ";

  /**
   * A bad commit message that has a subject line length is more that 100 chars.
   */
  public static final String BAD_COMMIT_MSG_SUBJECT_LINE_LENGTH_IS_LONGER_THAN_100 =
      "feat: testing for the first poc added poc notes on the readme file and another file and another file and another file";

  /**
   * A bad commit message that does not start with new line.
   */
  public static final String BAD_COMMIT_MSG_BODY_NOT_STARTING_WITH_NEWLINE = "feat: testing for the first poc added poc notes on the readme file\nand another file and another file and another file";

}
