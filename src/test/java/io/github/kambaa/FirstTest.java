package io.github.kambaa;

import io.github.kambaa.utils.Utils;
import java.io.File;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.junit.Before;
import org.junit.Test;

/**
 * https://cwiki.apache.org/confluence/display/MAVENOLD/Maven+Plugin+Harness
 * https://maven.apache.org/plugin-developers/plugin-testing.html
 */
public class FirstTest extends AbstractMojoTestCase {

  private CheckFile mojo;

  @Before
  protected void setUp() throws Exception {
    super.setUp();

    String fileLocation = "src/test/resources/test-project/test-pom.xml";
    File testPom = new File(getBasedir(),
        fileLocation);
    assertNotNull(testPom);
    assertTrue(testPom.exists());
    mojo = (CheckFile) lookupMojo("checkFile", testPom);
    assertNotNull(mojo);
    mojo.setMavenDebug(true);
  }

  @Test
  public void testMojoConfiguration()
      throws Exception {

    mojo.execute();
    assertNotNull(mojo.getCommitMessageList());

    assertEquals(mojo.getFile(), "src/test/resources/test-project/test-git-log.txt");
    mojo.setFile("src/test/resources/test-project/test-git-log.txt");
    assertEquals(mojo.getFile(), "src/test/resources/test-project/test-git-log.txt");

    assertEquals(mojo.getSeparator(), Utils.DEFAULT_SEPARATOR);
    mojo.setSeparator(Utils.DEFAULT_SEPARATOR);
    assertEquals(mojo.getSeparator(), Utils.DEFAULT_SEPARATOR);

    assertNotNull(mojo.getCustomIgnorePatterns());
    assertEquals(mojo.getCustomIgnorePatterns().size(), 1);
    assertEquals(mojo.getCustomIgnorePatterns().get(0), "^(C|c)hore(\\([^)]+\\))?:");
    assertTrue(mojo.isEnableDefaultMessageChecking());
    assertTrue(mojo.isEnableDefaultCommitSubjectTypes());
    assertTrue(mojo.isEnableForcingSubjectEndsWithCommaErrorCheck());
    assertFalse(mojo.isEnableForcingNewLineBetweenSubjectAndBodyCheck());
    assertNull(mojo.getCustomCommitSubjectTypes());
    assertNotNull(mojo.getCustomCheckingMethods());
    assertEquals(mojo.getCustomCheckingMethods().size(), 1);
    assertEquals(mojo.getCustomCheckingMethods().get(0), "io.github.kambaa.utils.Utils.dummyCheckMessage");

    assertEquals(57, mojo.getCommitMessageList().size());
    assertEquals(51, mojo.getIgnoreFilteredCommitMessageList().size());
  }

  @Test
  public void testMojoFailureOnFileWithNoContent() throws Exception {
    try {
      mojo.setFile("src/test/resources/test-project/negative-cases/empty-content.txt");
      mojo.execute();
      fail("An exception was expected due to the file being empty, but none was thrown.");
    } catch (Exception e) {
      assertTrue(e instanceof MojoFailureException);
    }

    try {
      mojo.setFile("src/test/resources/test-project/negative-cases/non-existing-file");
      mojo.execute();
      fail("An exception was expected due to the file not existing, but none was thrown.");
    } catch (Exception e) {
      assertTrue(e instanceof MojoFailureException);
    }

    try {
      mojo.setFile("src/test/resources/test-project/negative-cases/content-having-no-separator.txt");
      mojo.execute();
      fail("An exception was expected due to the file not existing, but none was thrown.");
    } catch (Exception e) {
      assertTrue(e instanceof MojoFailureException);
    }
  }

  @Test
  public void testMojoFailureOnNonExistingFile() throws Exception {
    try {
      mojo.setFile("src/test/resources/test-project/negative-cases/non-existing-file");
      mojo.execute();
      fail("An exception was expected due to the file not existing, but none was thrown.");
    } catch (Exception e) {
      assertTrue(e instanceof MojoFailureException);
    }
  }

  @Test
  public void testMojoFailureOnFileWithNoSeparator() throws Exception {
    try {
      mojo.setFile("src/test/resources/test-project/negative-cases/content-having-no-separator.txt");
      mojo.execute();
      fail("An exception was expected due to the file content having no separator, but none was thrown.");
    } catch (Exception e) {
      assertTrue(e instanceof MojoFailureException);
    }
  }

}