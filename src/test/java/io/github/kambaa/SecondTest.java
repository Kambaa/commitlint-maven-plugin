package io.github.kambaa;

import edu.emory.mathcs.backport.java.util.Arrays;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.junit.Before;
import org.junit.Test;

/**
 * Commit message string checking tests.
 */
public class SecondTest extends AbstractMojoTestCase {

  private CheckMessage mojo;

  private String goodCommitMsg = "feat: testing for the first poc\n"
                                 + "\n"
                                 + "added poc notes on the readme file";

  private String goodCommitMsgWithScopeAndBreakingChangeIndicator = "feat(general)!: testing for the first poc\n"
                                                                    + "\n"
                                                                    + "added poc notes on the readme file";

  private String badCommitMsgNotCompliesWithTheSplittingRegex = "feat testing for the first poc";

  private String badCommitMsgUnknownSubjectType = "feast: testing for the first poc.\n"
                                                  + "\n"
                                                  + "added poc notes on the readme file";

  private String badCommitMsgEndingWithPeriod = "feat: testing for the first poc.\n"
                                                + "\n"
                                                + "added poc notes on the readme file";

  private String badCommitMsgEmptyDescription = "feat: ";

  private String badCommitMsgSubjectLineLengthIsLongerThan100 = "feat: testing for the first poc added poc notes on the readme file and another file and another file and another file";
  private String badCommitMsgBodyNotStartingWithNewLine = "feat: testing for the first poc added poc notes on the readme file\nand another file and another file and another file";

  @Before
  protected void setUp() throws Exception {
    super.setUp();

    String fileLocation = "src/test/resources/test-project/test-pom2.xml";
    File testPom = new File(getBasedir(),
        fileLocation);
    assertNotNull(testPom);
    assertTrue(testPom.exists());
    mojo = (CheckMessage) lookupMojo("checkMsg", testPom);
    assertNotNull(mojo);
    mojo.setMavenDebug(true);
    assertTrue(mojo.isMavenDebug());
    mojo.setSkip(false);
    assertFalse(mojo.isSkip());
    mojo.setFailOnError(false);
    assertFalse(mojo.isFailOnError());

    assertNull(mojo.getCustomIgnorePatterns());
    assertNull(mojo.getCustomIgnorePatterns());
    assertTrue(mojo.isEnableDefaultMessageChecking());
    assertTrue(mojo.isEnableDefaultCommitSubjectTypes());
    assertTrue(mojo.isEnableForcingSubjectEndsWithCommaErrorCheck());
    mojo.setEnableDefaultIgnorePatterns(true);
    assertTrue(mojo.isEnableDefaultIgnorePatterns());
    assertFalse(mojo.isEnableForcingNewLineBetweenSubjectAndBodyCheck());
    assertNull(mojo.getCustomCommitSubjectTypes());

    List<String> customSubjects = Arrays.asList(new String[] {"wasd"});
    mojo.setCustomCommitSubjectTypes(customSubjects);
    assertNotNull(mojo.getCustomCommitSubjectTypes());
    assertEquals(customSubjects, mojo.getCustomCommitSubjectTypes());

    assertNull(mojo.getCustomCheckingMethods());
    assertNull(mojo.getCustomCheckingMethods());
  }

  @Test
  public void testGoodCommitMessage()
      throws Exception {

    mojo.setCommitMessage(goodCommitMsg);
    assertEquals(goodCommitMsg, mojo.getCommitMessage());
    mojo.execute();

    assertNotNull(mojo.getSubjectTypeList());

    mojo.setCommitMessage(goodCommitMsgWithScopeAndBreakingChangeIndicator);
    assertEquals(goodCommitMsgWithScopeAndBreakingChangeIndicator, mojo.getCommitMessage());
    mojo.execute();
  }

  @Test
  public void testNullCommitMessage()
      throws Exception {
    try {
      mojo.setCommitMessage(null);
      mojo.execute();
      fail("An exception was expected due to the commit message being null, but none was thrown.");
    } catch (Exception e) {
      assertTrue(e instanceof MojoFailureException);
    }
  }

  @Test
  public void testEmptyCommitMessage()
      throws Exception {
    try {
      mojo.setCommitMessage("");
      mojo.execute();
      fail("An exception was expected due to the commit message being null, but none was thrown.");
    } catch (Exception e) {
      assertTrue(e instanceof MojoFailureException);
    }
  }

  @Test
  public void testBadCommitMessageNotSplittableWithRegex()
      throws Exception {
    try {
      mojo.setCommitMessage(badCommitMsgNotCompliesWithTheSplittingRegex);
      mojo.execute();
      fail("An exception was expected due to the commit message ending with period, but none was thrown.");
    } catch (Exception e) {
      assertTrue(e instanceof MojoFailureException);
    }
  }

  @Test
  public void testBadCommitMessageUnknownSubjectType()
      throws Exception {
    try {
      mojo.setCommitMessage(badCommitMsgUnknownSubjectType);
      mojo.execute();
      fail("An exception was expected due to the commit message ending with period, but none was thrown.");
    } catch (Exception e) {
      assertTrue(e instanceof MojoFailureException);
    }
  }

  @Test
  public void testBadCommitMessageEmptyDescription()
      throws Exception {
    try {
      mojo.setCommitMessage(badCommitMsgEmptyDescription);
      mojo.execute();
      fail("An exception was expected due to the commit message ending with period, but none was thrown.");
    } catch (Exception e) {
      assertTrue(e instanceof MojoFailureException);
    }
  }

  @Test
  public void testBadCommitMessageEndingWithPeriod()
      throws Exception {
    // When set to false, just a warning printed.
    mojo.setCommitMessage(badCommitMsgEndingWithPeriod);
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

  @Test
  public void testBadCommitMessageSubjectLineLength()
      throws Exception {
    try {
      mojo.setCommitMessage(badCommitMsgSubjectLineLengthIsLongerThan100);
      mojo.execute();
      fail("An exception was expected due to the commit message ending with period, but none was thrown.");
    } catch (Exception e) {
      assertTrue(e instanceof MojoFailureException);
    }
  }

  @Test
  public void testCommitMessageBodyStartingWithNewLine()
      throws Exception {

    // When false, just prints warning.
    mojo.setCommitMessage(badCommitMsgBodyNotStartingWithNewLine);
    mojo.setEnableForcingNewLineBetweenSubjectAndBodyCheck(false);
    mojo.execute();

    // When true it fails.
    try {
      mojo.setEnableForcingNewLineBetweenSubjectAndBodyCheck(true);
      assertTrue(mojo.isEnableForcingNewLineBetweenSubjectAndBodyCheck());
      mojo.execute();
      fail("An exception was expected due to the commit message ending with period, but none was thrown.");
    } catch (Exception e) {
      assertTrue(e instanceof MojoFailureException);
    }
  }

  @Test
  public void testOtherMethods()
      throws Exception {
    mojo.setCommitMessage(goodCommitMsg);

    List<String> customMethodNames = new ArrayList<>();
    customMethodNames.add("io.github.kambaa.DummyClass.dummyMethod");
    mojo.setCustomCheckingMethods(customMethodNames);
    assertEquals(customMethodNames, mojo.getCustomCheckingMethods());
    mojo.execute();

    List<String> customIgnorePatterns = new ArrayList<>();
    customIgnorePatterns.add("\\n");
    mojo.setCustomIgnorePatterns(customIgnorePatterns);
    assertEquals(customIgnorePatterns, mojo.getCustomIgnorePatterns());
    mojo.execute();
    assertNotNull(mojo.getIgnorePatterns());

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