package io.github.kambaa;

import static io.github.kambaa.utils.Utils.isEmpty;

import io.github.kambaa.utils.MyBaseMojo;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

// todo: add custom checking method. https://github.com/Kambaa/gmc-maven-plugin/blob/1323d032130f067eaab41ba7ee9b17619699c464/src/main/java/dev/kambaabi/MyMojo.java

/**
 * Checks commit message.
 */
@Mojo(name = "checkFile")
public class CheckFile extends MyBaseMojo {

  /**
   * Constructor.
   */
  public CheckFile() {
  }

  /**
   * File name containing the commit messages.
   * File must be generated via `git log` command and placed within the project folder
   */
  @Parameter(required = true)
  private String file;

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
    commitMessageList = splitCommitMessagesFromGivenLogString(checkAndReadGivenFile(), separator);
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

  /**
   * getFile.
   *
   * @return file
   */
  public String getFile() {
    return file;
  }

  /**
   * setFile.
   *
   * @param file file
   */
  public void setFile(String file) {
    this.file = file;
  }

  /**
   * getSeparator.
   *
   * @return separator
   */
  public String getSeparator() {
    return separator;
  }

  /**
   * setSeparator.
   *
   * @param separator separator
   */
  public void setSeparator(String separator) {
    this.separator = separator;
  }

  /**
   * getCommitMessageList.
   *
   * @return commitMessageList
   */
  public List<String> getCommitMessageList() {
    return commitMessageList;
  }

  /**
   * getIgnoreFilteredCommitMessageList.
   *
   * @return ignoreFilteredCommitMessageList
   */
  public List<String> getIgnoreFilteredCommitMessageList() {
    return ignoreFilteredCommitMessageList;
  }

}
