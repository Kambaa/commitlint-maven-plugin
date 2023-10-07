package io.github.kambaa;

import java.io.File;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * GitLogValidationTest.
 */
public class GitLogValidationTest extends AbstractMojoTestCase {

  @Before
  public void setUp() throws Exception {
    super.setUp();
  }

  @Test
  public void testMojoConfiguration()
      throws Exception {
    File testPom = new File(getBasedir(),
        "src/test/resources/test-project/pom2.xml");
    Assert.assertNotNull(testPom);
    Assert.assertTrue(testPom.exists());
    CheckCommitMsg mojo = (CheckCommitMsg) lookupMojo("check", testPom);
    Assert.assertNotNull(mojo.getMultipleGitLogMessages());
    Assert.assertNotNull(mojo.getMultipleGitLogMessageSeparator());

    // TODO: add whitelisting for commits like: `Merge branch 'master' of https://github.com/Kambaa/gmc-maven-plugin`
    // TODO: add
    // org.apache.maven.plugin.MojoFailureException: Commit message can not be parsed!
    //     Start by entering a commit message like this:
    // feat: add hat wobble
    // Your commit message first line: `Merge branch 'master' of https://github.com/Kambaa/gmc-maven-plugin`
    // Commit message is: `Merge branch 'master' of https://github.com/Kambaa/gmc-maven-plugin`
    //
    // at io.github.kambaa.utils.MyBaseMojo.exit(MyBaseMojo.java:198)
    // at io.github.kambaa.utils.MyBaseMojo.checkCommitMsg(MyBaseMojo.java:63)
    // at io.github.kambaa.CheckCommitMsg.execute(CheckCommitMsg.java:85)
    // mojo.execute();
  }

}