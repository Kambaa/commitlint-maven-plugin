package io.github.kambaa;

import java.io.File;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.junit.Before;
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

  @Test
  public void testMojoConfiguration()
      throws Exception {
    File testPom = new File(getBasedir(),
        "src/test/resources/test-project/pom.xml");
    assertNotNull(testPom);
    assertTrue(testPom.exists());
    CheckCommitMsg mojo = (CheckCommitMsg) lookupMojo("check", testPom);
    assertNotNull(mojo);
    assertEquals(false, mojo.isSkip());
    System.out.println("skip OK");
    assertEquals(true, mojo.isFailOnError());
    System.out.println("failOnErr OK");
    mojo.execute();
  }

}