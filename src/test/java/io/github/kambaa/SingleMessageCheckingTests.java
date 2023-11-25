package io.github.kambaa;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.testing.MojoRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * A working junit 4 test.
 * https://maven.apache.org/plugin-testing/maven-plugin-testing-harness/getting-started/index.html#create-a-mymojotest
 */
public class SingleMessageCheckingTests {

  @Rule
  public MojoRule rule = new MojoRule();

  private CheckMessage mojo;

  @Before
  public void init() throws Exception {
    File testPom = new File("src\\test\\resources\\test-project").getAbsoluteFile();
    assertNotNull(testPom);
    assertTrue(testPom.exists());
    mojo = (CheckMessage) rule.lookupConfiguredMojo(testPom, "checkMsg");
  }

  /**
   * shouldPassGoodCommitMessage.
   *
   * @throws Exception
   */
  @Test
  public void shouldPassGoodCommitMessage() throws MojoFailureException {
    mojo.setCommitMessage(TestCommitMessages.GOOD_COMMIT_MSG);
    assertEquals(TestCommitMessages.GOOD_COMMIT_MSG, mojo.getCommitMessage());
    mojo.execute();
  }

  /**
   * shouldPassGoodCommitMessageWithScopeAndBreakingChange.
   */
  @Test
  public void shouldPassGoodCommitMessageWithScopeAndBreakingChange() throws Exception {
    mojo.setCommitMessage(TestCommitMessages.GOOD_COMMIT_MSG_WITH_SCOPE_AND_BREAKING_CHANGE_INDICATOR);
    assertEquals(TestCommitMessages.GOOD_COMMIT_MSG_WITH_SCOPE_AND_BREAKING_CHANGE_INDICATOR, mojo.getCommitMessage());
    mojo.execute();
  }

  /**
   * shouldFailEmptyCommitMessage.
   *
   * @throws Exception
   */
  @Test
  public void shouldFailNullCommitMessage() throws Exception {
    try {
      mojo.setCommitMessage(null);
      mojo.execute();
      fail("An exception was expected due to the commit message being null, but none was thrown.");
    } catch (Exception e) {
      assertTrue(e instanceof MojoFailureException);
    }
  }

  /**
   * shouldFailEmptyCommitMessage.
   *
   * @throws Exception
   */
  @Test
  public void shouldFailEmptyCommitMessage()
      throws Exception {
    try {
      mojo.setCommitMessage("");
      mojo.execute();
      fail("An exception was expected due to the commit message being empty, but none was thrown.");
    } catch (Exception e) {
      assertTrue(e instanceof MojoFailureException);
    }
  }

  /**
   * shouldFailBadCommitMessageNotComplyingWithTheSplittingRegex.
   *
   * @throws Exception
   */
  @Test
  public void shouldFailNotComplyingWithTheSplittingRegex()
      throws Exception {
    try {
      mojo.setCommitMessage(TestCommitMessages.BAD_COMMIT_MSG_NOT_COMPLIES_WITH_THE_SPLITTING_REGEX);
      mojo.execute();
      fail("An exception was expected due to the commit message ending with period, but none was thrown.");
    } catch (Exception e) {
      assertTrue(e instanceof MojoFailureException);
    }
  }

  /**
   * shouldFailBadCommitMessageUnknownSubjectType.
   *
   * @throws Exception
   */
  @Test
  public void shouldFailUnknownSubjectType() throws Exception {
    try {
      mojo.setCommitMessage(TestCommitMessages.BAD_COMMIT_MSG_UNKNOWN_SUBJECT_TYPE);
      mojo.execute();
      fail("An exception was expected due to the commit message ending with period, but none was thrown.");
    } catch (Exception e) {
      assertTrue(e instanceof MojoFailureException);
    }
  }

  /**
   * shouldFailBadCommitMessageEmptyDescription.
   *
   * @throws Exception
   */
  @Test
  public void shouldFailEmptyDescription() throws Exception {
    try {
      mojo.setCommitMessage(TestCommitMessages.BAD_COMMIT_MSG_EMPTY_DESCRIPTION);
      mojo.execute();
      fail("An exception was expected due to the commit message ending with period, but none was thrown.");
    } catch (Exception e) {
      assertTrue(e instanceof MojoFailureException);
    }
  }

  /**
   * shouldFailBadCommitMessageEmptyDescription.
   *
   * @throws Exception
   */
  @Test
  public void shouldFailEndingWithPeriod()
      throws Exception {
    // When set to false, just a warning printed.
    mojo.setCommitMessage(TestCommitMessages.BAD_COMMIT_MSG_ENDING_WITH_PERIOD);
    mojo.setEnableForcingSubjectEndsWithCommaErrorCheck(false);
    assertFalse(mojo.isEnableForcingNewLineBetweenSubjectAndBodyCheck());
    mojo.execute();

    // When set to true, it should fail.
    try {
      mojo.setEnableForcingSubjectEndsWithCommaErrorCheck(true);
      mojo.execute();
      fail("An exception was expected due to the commit message ending with period, but none was thrown.");
    } catch (Exception e) {
      assertTrue(e instanceof MojoFailureException);
    }
  }

  /**
   * shouldFailSubjectLineLengthLongerThan100.
   *
   * @throws Exception
   */
  @Test
  public void shouldFailSubjectLineLengthLongerThan100()
      throws Exception {
    try {
      mojo.setCommitMessage(TestCommitMessages.BAD_COMMIT_MSG_SUBJECT_LINE_LENGTH_IS_LONGER_THAN_100);
      mojo.execute();
      fail("An exception was expected due to the commit message ending with period, but none was thrown.");
    } catch (Exception e) {
      assertTrue(e instanceof MojoFailureException);
    }
  }

  /**
   * shouldFailBodyNotStartingWithNewLineWhenEnabledInConfig.
   *
   * @throws Exception
   */
  @Test
  public void shouldFailBodyNotStartingWithNewLineWhenEnabledInConfig()
      throws Exception {
    try {
      mojo.setCommitMessage(TestCommitMessages.BAD_COMMIT_MSG_BODY_NOT_STARTING_WITH_NEWLINE);
      mojo.setEnableForcingNewLineBetweenSubjectAndBodyCheck(true);
      assertTrue(mojo.isEnableForcingNewLineBetweenSubjectAndBodyCheck());
      mojo.execute();
      fail("An exception was expected due to the commit message ending with period and set in configuration, but none was thrown.");
    } catch (Exception e) {
      assertTrue(e instanceof MojoFailureException);
    }
  }

  /**
   * shouldPassBodyNotStartingWithNewLineWhenDisabledInConfig.
   *
   * @throws Exception
   */
  @Test
  public void shouldPassBodyNotStartingWithNewLineWhenDisabledInConfig()
      throws Exception {
    // When false, just prints warning.
    mojo.setCommitMessage(TestCommitMessages.BAD_COMMIT_MSG_BODY_NOT_STARTING_WITH_NEWLINE);
    mojo.setEnableForcingNewLineBetweenSubjectAndBodyCheck(false);
    mojo.execute();
  }

  @Test
  public void testOtherMethods()
      throws Exception {

  }

}
