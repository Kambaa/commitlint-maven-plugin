package io.github.kambaa;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import edu.emory.mathcs.backport.java.util.Arrays;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.testing.MojoRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * MojoConfigurationTests.
 * junit 4 test.
 * https://maven.apache.org/plugin-testing/maven-plugin-testing-harness/getting-started/index.html#create-a-mymojotest
 */
public class MojoConfigurationTests {

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
   * test for a non-null mojo after initialization.
   *
   * @throws MojoFailureException
   */
  @Test
  public void shouldMojoBeNonNullWorkCorrectly() throws MojoFailureException {
    assertNotNull(mojo);
  }

  /**
   * test setting debug mode.
   *
   * @throws MojoFailureException
   */
  @Test
  public void shouldSetDebugModeWorkCorrectly() throws MojoFailureException {
    mojo.setMavenDebug(true);
    assertTrue(mojo.isMavenDebug());
  }

  /**
   * test setting skipping config.
   *
   * @throws MojoFailureException
   */
  @Test
  public void shouldDefaultValueAndSettingSkippingWorkCorrectly() throws MojoFailureException {
    assertFalse(mojo.isSkip());
    mojo.setSkip(true);
    assertTrue(mojo.isSkip());
    // Rollback to default
    mojo.setSkip(false);
  }

  /**
   * test setting fail on error config.
   *
   * @throws MojoFailureException
   */
  @Test
  public void shouldDefaultValueAndSettingFailOnErrorWorkCorrectly() throws MojoFailureException {
    assertTrue(mojo.isFailOnError());
    mojo.setFailOnError(false);
    assertFalse(mojo.isFailOnError());
    // Rollback to default
    mojo.setFailOnError(true);
  }

  /**
   * test setting a commit message.
   *
   * @throws MojoFailureException
   */
  @Test
  public void shouldSettingCommitMsgWorkCorrectly() throws MojoFailureException {
    mojo.setCommitMessage(TestCommitMessages.GOOD_COMMIT_MSG);
    assertEquals(TestCommitMessages.GOOD_COMMIT_MSG, mojo.getCommitMessage());
  }

  /**
   * test defaults and setting custom commit message checking methods work correctly.
   *
   * @throws MojoFailureException
   */
  @Test
  public void shouldDefaultValueAndSettingCustomCheckingMethodWorkCorrectly() throws MojoFailureException {
    // check defaults of custom ignore patterns.
    assertNotNull(mojo.getCustomCheckingMethods());
    assertEquals(0, mojo.getCustomCheckingMethods().size());

    // check adding a custom message checking method
    List<String> customMethodNames = new ArrayList<>();
    customMethodNames.add("io.github.kambaa.DummyClass.dummyMethod");
    mojo.setCustomCheckingMethods(customMethodNames);
    assertEquals(customMethodNames, mojo.getCustomCheckingMethods());
  }

  /**
   * test setting default message checking.
   *  todo: what was this for again? try to remember and remove this todo after.
   *
   * @throws MojoFailureException
   */
  @Test
  public void shouldSettingEnableDefaultMessageCheckingWorkCorrectly() throws MojoFailureException {
    assertTrue(mojo.isEnableDefaultMessageChecking());
    mojo.setEnableDefaultMessageChecking(false);
    assertFalse(mojo.isEnableDefaultMessageChecking());
  }

  /**
   * test default true value and setting of forcing subject line not end with comma config.
   *
   * @throws MojoFailureException
   */
  @Test
  public void shouldDefaultValueAndSettingForcingSubjectLineNotEndWithCommaWorkCorrectly() throws MojoFailureException {
    assertTrue(mojo.isEnableForcingSubjectEndsWithCommaErrorCheck());
    mojo.setEnableForcingSubjectEndsWithCommaErrorCheck(false);
    assertFalse(mojo.isEnableForcingSubjectEndsWithCommaErrorCheck());
    mojo.setEnableForcingSubjectEndsWithCommaErrorCheck(true);
  }

  /**
   * test default true value and setting of forcing a new line between subject and body config.
   *
   * @throws MojoFailureException
   */
  @Test
  public void shouldDefaultValueAndSettingNewLineBetweenSubjectAndBodyWorkCorrectly() throws MojoFailureException {
    assertFalse(mojo.isEnableForcingNewLineBetweenSubjectAndBodyCheck());
    mojo.setEnableForcingSubjectEndsWithCommaErrorCheck(true);
    assertTrue(mojo.isEnableForcingSubjectEndsWithCommaErrorCheck());
    mojo.setEnableForcingSubjectEndsWithCommaErrorCheck(false);
  }

  /**
   * test default true value and setting of using default ignore patterns config.
   *
   * @throws MojoFailureException
   */
  @Test
  public void shouldDefaultValueAndSettingUsingDefaultIgnorePatternsWorkCorrectly() throws MojoFailureException {
    assertTrue(mojo.isEnableDefaultIgnorePatterns());
    mojo.setEnableDefaultIgnorePatterns(false);
    assertFalse(mojo.isEnableDefaultIgnorePatterns());
    mojo.setEnableDefaultIgnorePatterns(true);
  }

  /**
   * test default true value and setting  enabling usage of default commit subject types config.
   *
   * @throws MojoFailureException
   */
  @Test
  public void shouldDefaultValueAndSettingEnableDefaultSubjectTypesWorkCorrectly() throws MojoFailureException {
    assertTrue(mojo.isEnableDefaultCommitSubjectTypes());
    mojo.setEnableDefaultCommitSubjectTypes(false);
    assertFalse(mojo.isEnableDefaultCommitSubjectTypes());
    mojo.setEnableDefaultCommitSubjectTypes(true);
  }

  /**
   * test defaults and setting custom subject type work correctly.
   *
   * @throws MojoFailureException
   */
  @Test
  public void shouldDefaultsAndSettingCustomSubjectTypesWorkCorrectly() throws MojoFailureException {
    // test default value is an empty array for the custom subject type list config.
    assertNotNull(mojo.getCustomCommitSubjectTypes());
    assertEquals(0, mojo.getCustomCommitSubjectTypes().size());

    // test adding custom subjects
    List<String> customSubjects = Arrays.asList(new String[] {"wasd"});
    mojo.setCustomCommitSubjectTypes(customSubjects);
    assertNotNull(mojo.getCustomCommitSubjectTypes());
    assertEquals(customSubjects, mojo.getCustomCommitSubjectTypes());

    // test total subject type count is correct(total of 12: 11 on default list and 1 given in as custom) after a successful execution
    mojo.setCommitMessage(TestCommitMessages.GOOD_COMMIT_MSG);
    mojo.execute();
    assertEquals(12, mojo.getSubjectTypeList().size());
  }

  /**
   * test defaults and setting custom ignore patterns work correctly.
   *
   * @throws MojoFailureException
   */
  @Test
  public void shouldDefaultsAndSettingCustomIgnorePatternsWorkCorrectly() throws MojoFailureException {
    // check defaults of custom ignore patterns.
    assertNotNull(mojo.getCustomIgnorePatterns());
    assertEquals(0, mojo.getCustomIgnorePatterns().size());

    // check adding custom ignore patterns and total list of ignore patterns to check.
    List<String> customIgnorePatterns = new ArrayList<>();
    customIgnorePatterns.add("\\n");
    mojo.setCustomIgnorePatterns(customIgnorePatterns);
    assertEquals(customIgnorePatterns, mojo.getCustomIgnorePatterns());

    // Check after a successful execution, total ignore patterns are correct
    // with addition of custom ones
    mojo.setCommitMessage(TestCommitMessages.GOOD_COMMIT_MSG);
    mojo.execute();
    assertNotNull(mojo.getIgnorePatterns());
    assertEquals(10, mojo.getIgnorePatterns().size());

    // Check for failure after configuring an invalid ignore pattern.
    try {
      customIgnorePatterns.add("(abc");
      mojo.setCustomIgnorePatterns(customIgnorePatterns);
      mojo.execute();
      fail("An exception was expected due to the given invalid custom ignore pattern, but none was thrown.");
    } catch (Exception e) {
      assertTrue(e instanceof MojoFailureException);
    }
  }

}
