package io.github.kambaa;

import static io.github.kambaa.utils.Utils.isEmpty;

import io.github.kambaa.utils.MyBaseMojo;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Checks commit message.
 */
@Mojo(name = "check")
public class CheckCommitMsg extends MyBaseMojo {
  /**
   * Constructor.
   */
  public CheckCommitMsg() {
  }

  /**
   * Default splitting text(without quotes).
   * "\n------------------------ &gt;8 ------------------------\n"
   */
  public static final String DEFAULT_SEPARATOR = "\n------------------------ >8 ------------------------\n";

  /**
   * To enter check multiple git log messages.
   */
  @Parameter
  private String multipleGitLogMessages;

  /**
   * To enter custom splitting text.
   * default splitter is(without quotes):
   * "\n------------------------ &gt;8 ------------------------\n"
   */
  @Parameter(defaultValue = DEFAULT_SEPARATOR)
  private String multipleGitLogMessageSeparator = DEFAULT_SEPARATOR;

  /**
   * Test your commit messages with this parameter value.
   */
  @Parameter
  private String testCommitMessage = "";

  @Override
  public void execute() throws MojoFailureException {
    if (skip) {
      info("Skip parameter is given true, skipping.");
      return;
    }
    if (isEmpty(testCommitMessage)) {
      debug("No commit message provided, skipping.");
    } else {
      info("Single commit message provided just testing that.");
      super.checkCommitMsg(testCommitMessage);
      return;
    }

    Path path = Paths.get(multipleGitLogMessages);
    String text = multipleGitLogMessages;
    boolean isGivenTextAFileLocation = Files.exists(path);
    if (isGivenTextAFileLocation) {
      try {
        text = new String(Files.readAllBytes(path));
      } catch (IOException ex) {
        exit("Error occured when trying to read from the file:%s\n\tException is: %s", multipleGitLogMessages, ex.getMessage());
        return;
      }
    }
    String[] commitMsgArr = text.split(multipleGitLogMessageSeparator);
    if (isEmpty(commitMsgArr)) {
      exit("Multiple Git Log Messages Can Not Be Parsed!\nSplitter%s\nGiven Multiple Log Messages:%s", multipleGitLogMessageSeparator, multipleGitLogMessages);
      return;
    }
    info("Starting to check given git log messages!");
    for (String msg : commitMsgArr) {
      if (isGivenTextAFileLocation) {
        debug("Trimming new lines from %s: ", msg);
        super.checkCommitMsg(msg.replaceAll("(?m)^\\s*", "").replaceAll("(?m)\\s*$", ""));
      } else {
        super.checkCommitMsg(msg);
      }
    }
    info("Checks done!");
  }

  /**
   * testCommitMessage getter.
   *
   * @return String
   */
  public String getTestCommitMessage() {
    return testCommitMessage;
  }

  /**
   * testCommitMessage setter.
   *
   * @param testCommitMessage testCommitMessage
   */
  public void setTestCommitMessage(String testCommitMessage) {
    this.testCommitMessage = testCommitMessage;
  }

  /**
   * multipleGitLogMessages getter.
   *
   * @return String
   */
  public String getMultipleGitLogMessages() {
    return multipleGitLogMessages;
  }

  /**
   * multipleGitLogMessages setter.
   *
   * @param multipleGitLogMessages multipleGitLogMessages
   */
  public void setMultipleGitLogMessages(String multipleGitLogMessages) {
    this.multipleGitLogMessages = multipleGitLogMessages;
  }

  /**
   * multipleGitLogMessageSplitter getter.
   *
   * @return String
   */
  public String getMultipleGitLogMessageSeparator() {
    return multipleGitLogMessageSeparator;
  }

  /**
   * multipleGitLogMessageSplitter setter.
   *
   * @param multipleGitLogMessageSeparator multipleGitLogMessageSplitter
   */
  public void setMultipleGitLogMessageSeparator(String multipleGitLogMessageSeparator) {
    this.multipleGitLogMessageSeparator = multipleGitLogMessageSeparator;
  }
}
