package io.github.kambaa;

import static io.github.kambaa.utils.Utils.isEmpty;

import io.github.kambaa.utils.MyBaseMojo;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Checks commit message.
 */
@Mojo(name = "check")
public class CheckCommitMsg extends MyBaseMojo {
  public static final String DEFAULT_SPLITTER = "------------------------ >8 ------------------------";

  /**
   * To enter check multiple git log messages.
   */
  @Parameter(required = true)
  private String multipleGitLogMessages;

  /**
   * To enter custom splitting text.
   */
  @Parameter(defaultValue = DEFAULT_SPLITTER)
  private String multipleGitLogMessageSplitter;

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
      debug("Single commit message provided just testing that.");
      super.checkCommitMsg(testCommitMessage);
      return;
    }

    String[] commitMsgArr = multipleGitLogMessages.split(multipleGitLogMessageSplitter);
    if (isEmpty(commitMsgArr)) {
      exit("Multiple Git Log Messages Can Not Be Parsed!\nSplitter%s\nGiven Multiple Log Messages:%s", multipleGitLogMessageSplitter, multipleGitLogMessages);
      return;
    }
    info("Starting to check given git log messages!");
    for (String msg : commitMsgArr) {
      super.checkCommitMsg(msg);
    }
    info("Checks done!");
  }

  public String getTestCommitMessage() {
    return testCommitMessage;
  }

  public void setTestCommitMessage(String testCommitMessage) {
    this.testCommitMessage = testCommitMessage;
  }

  public String getMultipleGitLogMessages() {
    return multipleGitLogMessages;
  }

  public void setMultipleGitLogMessages(String multipleGitLogMessages) {
    this.multipleGitLogMessages = multipleGitLogMessages;
  }

  public String getMultipleGitLogMessageSplitter() {
    return multipleGitLogMessageSplitter;
  }

  public void setMultipleGitLogMessageSplitter(String multipleGitLogMessageSplitter) {
    this.multipleGitLogMessageSplitter = multipleGitLogMessageSplitter;
  }
}
