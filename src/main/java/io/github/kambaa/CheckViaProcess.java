package io.github.kambaa;

import static io.github.kambaa.utils.Utils.DEFAULT_SEPARATOR;
import static io.github.kambaa.utils.Utils.isEmpty;

import io.github.kambaa.utils.MyBaseMojo;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Checks commit message from given piped data.
 */
@Mojo(name = "checkViaProcess")
public class CheckViaProcess extends MyBaseMojo {

  @Parameter(required = true)
  private String start;

  @Parameter(required = true)
  private String end;

  @Override
  public void execute() throws MojoFailureException {
    super.execute();
    // TODO: enhance, refactor git command and add commit validation
    String commandOutput = getGitLogOutput(getStart(), getEnd());
  }

  public String getGitLogOutput(String startCommit, String endCommit) throws MojoFailureException {
    try {
      List<String> command = new ArrayList<>(Arrays.asList("git", "log", "--format=%B" + DEFAULT_SEPARATOR));
      if (startCommit != null) {
        command.add(startCommit + ".." + (isEmpty(endCommit) ? "HEAD" : endCommit));
      }

      Process process = new ProcessBuilder(command).redirectErrorStream(true).start();

      StringBuilder outputBuilder = new StringBuilder();
      try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
        String line;
        while ((line = reader.readLine()) != null) {
          outputBuilder.append(line).append("\n");
        }
      }

      int exitCode = process.waitFor();
      if (exitCode != 0) {
        getLog().error("Error executing git log command: exit code " + exitCode);
        throw new MojoExecutionException("Git log command failed");
      }
      return outputBuilder.toString().trim();
    } catch (Exception e) {
      throw new MojoFailureException("Error retrieving git log output", e);
    }
  }

  public String getStart() {
    return start;
  }

  public void setStart(String start) {
    this.start = start;
  }

  public String getEnd() {
    return end;
  }

  public void setEnd(String end) {
    this.end = end;
  }
}