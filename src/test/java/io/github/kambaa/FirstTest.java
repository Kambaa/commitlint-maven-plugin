package io.github.kambaa;

import java.io.File;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.apache.maven.plugin.testing.MojoRule;
import org.apache.maven.plugin.testing.resources.TestResources;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * https://cwiki.apache.org/confluence/display/MAVENOLD/Maven+Plugin+Harness
 * https://maven.apache.org/plugin-developers/plugin-testing.html
 */
public class FirstTest extends AbstractMojoTestCase {

  @Before
  protected void setUp() throws Exception {
    super.setUp();
  }

  @Rule
  public MojoRule rule = new MojoRule();

  @Rule
  public TestResources resources = new TestResources();

  @Test
  public void testMojoConfiguration()
      throws Exception {
    File testPom = new File(getBasedir(),
        "src/test/resources/test-project/test-pom.xml");
    assertNotNull(testPom);
    assertTrue(testPom.exists());
    CheckFile mojo = (CheckFile) lookupMojo("checkFile", testPom);
    assertNotNull(mojo);
    mojo.setMavenDebug(true);
    mojo.execute();
    assertNotNull(mojo.getCommitMessageList());
    assertEquals(57, mojo.getCommitMessageList().size());
    assertEquals(52, mojo.getIgnoreFilteredCommitMessageList().size());
  }

}