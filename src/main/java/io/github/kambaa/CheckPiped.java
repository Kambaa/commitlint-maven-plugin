package io.github.kambaa;

import io.github.kambaa.utils.MyBaseMojo;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

/**
 * Checks commit message from given piped data.
 */
@Mojo(name = "checkPiped")
public class CheckPiped extends MyBaseMojo {

  private String commitMessage;

  @Override
  public void execute() throws MojoFailureException {
    super.execute();
    try {
      BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
      String line;
      StringBuilder sb = new StringBuilder();
      while ((line = reader.readLine()) != null) {
        sb.append(line);
        sb.append("\n");
      }
      commitMessage = sb.toString();
      checkCommitMessage(commitMessage);
    } catch (Exception ex) {
      throw new MojoFailureException("Error processing piped data", ex);
    }
  }

  public String getCommitMessage() {
    return commitMessage;
  }
}
