import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import io.github.kambaa.CheckFile;
import io.github.kambaa.utils.Utils;
import java.io.File;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.testing.MojoRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * FileCheckingTests.
 * junit 4 test.
 * https://maven.apache.org/plugin-testing/maven-plugin-testing-harness/getting-started/index.html#create-a-mymojotest
 */
public class FileCheckingTests {

  @Rule
  public MojoRule rule = new MojoRule();

  private CheckFile mojo;

  @Before
  public void init() throws Exception {
    File testPom = new File("src\\test\\resources\\test-project").getAbsoluteFile();
    assertNotNull(testPom);
    assertTrue(testPom.exists());
    mojo = (CheckFile) rule.lookupConfiguredMojo(testPom, "checkFile");
    assertNotNull(mojo);
    mojo.setMavenDebug(true);
  }

  /**
   * shouldDefaultAndSettingFileNameConfigWorkCorrectly.
   *
   * @throws MojoFailureException
   */
  @Test
  public void shouldDefaultAndSettingFileNameConfigWorkCorrectly() throws MojoFailureException {
    assertNull(mojo.getFile());
    mojo.setFile("src/test/resources/test-git-log-correct.txt");
    assertEquals("src/test/resources/test-git-log-correct.txt", mojo.getFile());
  }

  /**
   * shouldDefaultAndSettingSeparatorConfigWorkCorrectly.
   *
   * @throws MojoFailureException
   */
  @Test
  public void shouldDefaultAndSettingSeparatorConfigWorkCorrectly() throws MojoFailureException {
    assertEquals(Utils.DEFAULT_SEPARATOR, mojo.getSeparator());
    String dummySeparator = "|||DUMMY_SEPARATOR|||";
    mojo.setSeparator(dummySeparator);
    assertEquals(dummySeparator, mojo.getSeparator());
  }

  /**
   * shouldIgnoreFilterWorkingCorrectly.
   *
   * @throws MojoFailureException
   */
  @Test
  public void shouldIgnoreFilterWorkingCorrectly() throws MojoFailureException {
    assertNull(mojo.getCommitMessageList());
    assertNull(mojo.getIgnoreFilteredCommitMessageList());
    mojo.setFile("src/test/resources/test-git-log-correct.txt");
    mojo.execute();
    assertNotNull(mojo.getCommitMessageList());
    assertNotNull(mojo.getIgnoreFilteredCommitMessageList());
    assertEquals(57, mojo.getCommitMessageList().size());
    assertEquals(51, mojo.getIgnoreFilteredCommitMessageList().size());
  }

  /**
   * shouldFailOnNonExistingFile.
   *
   * @throws MojoFailureException
   */
  @Test
  public void shouldFailOnNonExistingFile() throws MojoFailureException {
    try {
      mojo.setFile("src/test/resources/non-existing-file");
      mojo.execute();
      fail("An exception was expected due to the file not existing, but none was thrown.");
    } catch (Exception e) {
      assertTrue(e instanceof MojoFailureException);
    }
  }

  /**
   * shouldFailOnEmptyFile.
   *
   * @throws MojoFailureException
   */
  @Test
  public void shouldFailOnEmptyFile() throws MojoFailureException {
    try {
      mojo.setFile("src/test/resources/test-git-log-empty-content.txt");
      mojo.execute();
      fail("An exception was expected due to the file being empty, but none was thrown.");
    } catch (Exception e) {
      assertTrue(e instanceof MojoFailureException);
    }
  }

  /**
   * shouldFailOnFileWithNoSeparator.
   *
   * @throws MojoFailureException
   */
  @Test
  public void shouldFailOnFileWithNoSeparator() throws MojoFailureException {
    try {
      mojo.setFile("src/test/resources/test-git-log-content-having-no-separator.txt");
      mojo.execute();
      fail("An exception was expected due to the file not existing, but none was thrown.");
    } catch (Exception e) {
      assertTrue(e instanceof MojoFailureException);
    }
  }

}
