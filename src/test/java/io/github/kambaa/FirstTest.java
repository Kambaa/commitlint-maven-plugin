package io.github.kambaa;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
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
        "src/test/resources/test-project/pom.xml");
    assertNotNull(testPom);
    assertTrue(testPom.exists());
    GetDelimiter mojo = (GetDelimiter) lookupMojo("getDelimiter", testPom);
    assertNotNull(mojo);
    File testGitLog = new File(getBasedir(),
        "src/test/resources/test-project/test-git-log.txt");
    mojo.setTestCommitMessage(new String(Files.readAllBytes(testGitLog.toPath()), StandardCharsets.UTF_8));
    mojo.execute();
    assertNotNull(mojo.commitMessageList);
    assertEquals(56, mojo.commitMessageList.size());

  }

}