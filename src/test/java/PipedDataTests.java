import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import io.github.kambaa.CheckPiped;
import java.io.ByteArrayInputStream;
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
public class PipedDataTests {

  @Rule
  public MojoRule rule = new MojoRule();

  private CheckPiped mojo;

  @Before
  public void init() throws Exception {
    File testPom = new File("src/test/resources/test-project").getAbsoluteFile();
    assertNotNull(testPom);
    assertTrue(testPom.exists());
    mojo = (CheckPiped) rule.lookupConfiguredMojo(testPom, "checkPiped");
    assertNotNull(mojo);
    mojo.setMavenDebug(true);
  }

  /**
   * shouldWorkCorrectly.
   *
   * @throws MojoFailureException
   */
  @Test
  public void shouldWorkCorrectly() throws MojoFailureException {
    // Simulate piped data
    String data = "feat: add hat wobble\n\nanother body text after subject line.";
    System.setIn(new ByteArrayInputStream(data.getBytes()));
    // Execute the plugin
    mojo.execute();
    // Verify the plugin processed the data
    assertEquals(data + "\n", mojo.getCommitMessage());

    data = "feattt: invalid commit message";
    System.setIn(new ByteArrayInputStream(data.getBytes()));
    try {
      // Execute the plugin
      mojo.execute();
      fail("An exception was expected due to running from piped data with invalid commit message, but none was thrown.");
    } catch (Exception e) {
      assertTrue(e instanceof MojoFailureException);
    }
  }
}
