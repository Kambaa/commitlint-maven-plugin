package io.github.kambaa;

import io.github.kambaa.utils.MyBaseMojo;
import java.util.Map;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Checks commit message.
 */
@Mojo(name = "checkMsg"
    // ,
    // requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME,
    // defaultPhase = LifecyclePhase.COMPILE
)
public class CheckMessage extends MyBaseMojo {

  @Parameter
  private String commitMessage;

  @Override
  public void execute() throws MojoFailureException {
    super.execute();

    checkCommitMessage(commitMessage);
  }

  private void read(final Map<String, String> map, final String key) {
    final String property = System.getProperty(key);
    if (null != property) {
      map.put(key, property);
    }
  }

  public String getCommitMessage() {
    return commitMessage;
  }

  public void setCommitMessage(String commitMessage) {
    this.commitMessage = commitMessage;
  }
}
