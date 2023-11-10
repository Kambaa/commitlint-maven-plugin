package io.github.kambaa;

import static io.github.kambaa.utils.Utils.isEmpty;

import io.github.kambaa.utils.MyBaseMojo;
import io.github.kambaa.utils.Utils;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Checks commit message.
 */
// todo: add custom checking method. https://github.com/Kambaa/gmc-maven-plugin/blob/1323d032130f067eaab41ba7ee9b17619699c464/src/main/java/dev/kambaabi/MyMojo.java
@Mojo(name = "checkFile"
    // ,
    // requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME,
    // defaultPhase = LifecyclePhase.COMPILE
)
public class CheckFile extends MyBaseMojo {

  /**
   * File name containing the commit messages.
   * File must be generated via `git log` command and placed within the project folder
   */
  @Parameter(required = true)
  private String file;

  /**
   * Separator used in the git log command.
   * Default is {@see Utils.DEFAULT_SEPARATOR}
   */
  @Parameter(defaultValue = Utils.DEFAULT_SEPARATOR)
  private String separator = Utils.DEFAULT_SEPARATOR;

  /**
   * Raw Commit message list extracted from given file.
   */
  private List<String> commitMessageList;

  /**
   * Filtered out commit message list.
   */
  private List<String> ignoreFilteredCommitMessageList;

  @Override
  public void execute() throws MojoFailureException {
    super.execute();

    commitMessageList = splitCommitMessagesFromGitLog(checkAndReadGivenFile(), separator);
    String commitMessageDelim = "\n--> ";
    debug("Extracted %d commits:\n--> %s", commitMessageList.size(),
        String.join(commitMessageDelim, commitMessageList));
    ignoreFilteredCommitMessageList = filterOutIgnoredCommits(commitMessageList);
    debug("Remaining %d commit messages(filtered out ignored commits):\n--> %s",
        ignoreFilteredCommitMessageList.size(),
        String.join(commitMessageDelim, ignoreFilteredCommitMessageList)
    );

    for (String msg : ignoreFilteredCommitMessageList) {
      checkCommitMessage(msg);
    }

  }

  private List<String> filterOutIgnoredCommits(List<String> commitMessagesToCheck) {
    List<String> out = commitMessageList.stream()
        .filter(s -> ignorePatterns.stream()
            .noneMatch(pattern -> pattern.matcher(s).find())
        )
        .collect(Collectors.toList());
    if (mavenDebug) {
      List<String> ignoredCommits = commitMessagesToCheck.stream()
          .filter(element -> !out.contains(element))
          .collect(Collectors.toList());
      debug("These %d commits are filtered through ignore patterns:\n--> %s", ignoredCommits.size(), String.join("\n--> ", ignoredCommits));
    }
    return out;
  }

  /**
   * Splits given commit messages (generated via the git log command) by the given separator.
   *
   * @param gitLogMessages String value of git log --format='%B%n{GIVEN_SEPARATOR}'.
   * @param separator      separator used on the git log command.
   * @return List of strings that each one represents one of the commit messages.
   */
  private List<String> splitCommitMessagesFromGitLog(String gitLogMessages, String separator) throws MojoFailureException {
    gitLogMessages = gitLogMessages.replace("\r\n", "\n");
    if (!gitLogMessages.contains(separator)) {
      debug("Given file does not contain any separator string!");
      throw new MojoFailureException("Given file does not contain separator text are you sure you are giving the `git log` with separator command result?");
    }
    String[] commitMsgArr = gitLogMessages.split(separator);
    if (isEmpty(commitMsgArr)) {
      debug("Seperation result is empty, returning null!\n\tSeparator: %s\n\tGitLogMessages: %s ", separator, gitLogMessages);
      return Collections.emptyList();
    }
    List<String> out = Stream.of(commitMsgArr).map(s -> {
      // Trim new lines at the beginning and at the end of each split commit message(which is written by git log command.
      return s.replaceAll("(?m)^\\s*", "").replaceAll("(?m)\\s*$", "");
    }).collect(Collectors.toList());
    if (isEmpty(out)) {
      debug("Extraction result array is empty, throwing exception!");
      throw new MojoFailureException("No commit messages extracted from the given file!");
    }
    return out;
  }

  private String checkAndReadGivenFile() throws MojoFailureException {
    try {
      String content = new String(Files.readAllBytes((new File(file)).toPath()), StandardCharsets.UTF_8);
      if (isEmpty(content)) {
        throw new MojoFailureException("File is empty!");
      }
      return content;
    } catch (IOException exception) {
      debug("IOException occurred while opening given file: %s", file);
      if (mavenDebug) {
        exception.printStackTrace();
      }
      throw new MojoFailureException("Failed to read given file!\n\t" + exception.getMessage());
    }
  }

  public String getFile() {
    return file;
  }

  public void setFile(String file) {
    this.file = file;
  }

  public String getSeparator() {
    return separator;
  }

  public void setSeparator(String separator) {
    this.separator = separator;
  }
  public List<String> getCommitMessageList() {
    return commitMessageList;
  }

  public List<String> getIgnoreFilteredCommitMessageList() {
    return ignoreFilteredCommitMessageList;
  }

}
