package io.github.kambaa;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import edu.emory.mathcs.backport.java.util.Arrays;
import java.io.File;
import java.util.List;
import org.apache.maven.plugin.testing.MojoRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * A working junit 4 test.
 * https://maven.apache.org/plugin-testing/maven-plugin-testing-harness/getting-started/index.html#create-a-mymojotest
 */
public class ThirdTest {

  @Rule
  public MojoRule rule = new MojoRule();

  /**
   * shouldSuccessfullyExecuteUponCorrectCommitMessages.
   *
   * @throws Exception
   */
  @Test
  public void shouldSuccessfullyExecuteUponCorrectCommitMessages() throws Exception {
    File testPom = new File("src\\test\\resources\\test-project1").getAbsoluteFile();
    assertNotNull(testPom);
    assertTrue(testPom.exists());

    CheckMessage mojo = (CheckMessage) rule.lookupConfiguredMojo(testPom, "checkMsg");
    assertNotNull(mojo);
    mojo.setMavenDebug(true);
    assertTrue(mojo.isMavenDebug());
    mojo.setSkip(false);
    assertFalse(mojo.isSkip());
    mojo.setFailOnError(false);
    assertFalse(mojo.isFailOnError());

    mojo.setMavenDebug(true);
    assertTrue(mojo.isMavenDebug());
    mojo.setSkip(false);
    assertFalse(mojo.isSkip());
    mojo.setFailOnError(false);
    assertFalse(mojo.isFailOnError());

    assertNotNull(mojo.getCustomIgnorePatterns());
    assertEquals(0, mojo.getCustomIgnorePatterns().size());

    assertTrue(mojo.isEnableDefaultMessageChecking());
    assertTrue(mojo.isEnableDefaultCommitSubjectTypes());
    assertTrue(mojo.isEnableForcingSubjectEndsWithCommaErrorCheck());
    mojo.setEnableDefaultIgnorePatterns(true);
    assertTrue(mojo.isEnableDefaultIgnorePatterns());
    assertFalse(mojo.isEnableForcingNewLineBetweenSubjectAndBodyCheck());
    assertNotNull(mojo.getCustomCommitSubjectTypes());
    assertEquals(0, mojo.getCustomCommitSubjectTypes().size());

    List<String> customSubjects = Arrays.asList(new String[] {"wasd"});
    mojo.setCustomCommitSubjectTypes(customSubjects);
    assertNotNull(mojo.getCustomCommitSubjectTypes());
    assertEquals(customSubjects, mojo.getCustomCommitSubjectTypes());

    mojo.setCommitMessage(TestCommitMessages.GOOD_COMMIT_MSG);
    assertEquals(TestCommitMessages.GOOD_COMMIT_MSG, mojo.getCommitMessage());
    mojo.execute();

    assertNotNull(mojo.getSubjectTypeList());

    mojo.setCommitMessage(TestCommitMessages.GOOD_COMMIT_MSG_WITH_SCOPE_AND_BREAKING_CHANGE_INDICATOR);
    assertEquals(TestCommitMessages.GOOD_COMMIT_MSG_WITH_SCOPE_AND_BREAKING_CHANGE_INDICATOR, mojo.getCommitMessage());
    mojo.execute();

  }
}
