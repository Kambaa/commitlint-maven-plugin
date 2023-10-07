package io.github.kambaa.utils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Base mojo abstract class.
 */
public abstract class MyBaseMojo extends AbstractMojo {
  /**
   * Constructor.
   */
  public MyBaseMojo() {
  }

  /**
   * Skips execution if true.
   */
  @Parameter(defaultValue = "false")
  protected boolean skip;

  /**
   * If sets to false, validations errors will be be a build failure, instead messages will be logged as an error.
   */
  @Parameter(defaultValue = "true")
  protected boolean failOnError;

  /**
   * Common controls.
   */
  @Override
  public void execute() throws MojoFailureException {

  }

  /**
   * checks commit message.
   *
   * @param testCommitMessage given commit message text.
   * @throws MojoFailureException if any problems occur.
   */
  protected void checkCommitMsg(String testCommitMessage) throws MojoFailureException {
    String[] splitStr = testCommitMessage.split("\n");
    if (splitStr.length == 0) {
      exit("Commit message can not be parsed!");
    }
    debug("Capture Group Count: %d", splitStr.length);

    String subjectLine = splitStr[0];
    debug("Extracted subject is: %s", subjectLine);
    Pattern pattern = Pattern.compile("^(\\w*)(?:\\(([\\w\\-.]+)\\))?(!)?: (.*)(\\n[\\s\\S]*)?");
    Matcher subjectMatcher = pattern.matcher(subjectLine);
    if (!subjectMatcher.matches()) {
      debug("Are you adding new lines before commit?");
      debug("Subject Line is: %s", subjectLine);
      debug("Commit Message is: %s", testCommitMessage);
      exit("Commit message can not be parsed!\n\tStart by entering a commit message " +
           "like this:\n\tfeat: add hat wobble\n\tYour commit message first line: `" + subjectLine + "`");
    }

    String type = subjectMatcher.group(1);
    String scope = subjectMatcher.group(2);
    String breakingChangeIndicator = subjectMatcher.group(3);
    String description = subjectMatcher.group(4);
    debug("Extracted info from capture groups: ");
    debug("type: %s", type);
    debug("scope: %s", scope);
    debug("indicator for a breaking change: %s", breakingChangeIndicator);
    debug("description: %s", description);

    Set<String> types = new HashSet<>(Arrays.asList(
        "build",
        "chore",
        "ci",
        "docs",
        "feat",
        "fix",
        "perf",
        "refactor",
        "revert",
        "style",
        "test"));
    debug("checking type name: `%s`", type);

    if (!types.contains(type)) {
      exit("Commit message is not valid!\n\tWritten type(`" + type + "`) is not one of: " +
           "`" + String.join("`,`", types.toArray(new String[0])) + "`");
      return;
    }
    debug("done checking type name: %s", types);
    debug("checking scope %s", scope);
    if (null != scope) {
      warn("You have a scope on your commit message!");
    }
    debug("checking breakingChangeIndicator %s", breakingChangeIndicator);
    if (null != breakingChangeIndicator) {
      warn("You have an indicator for a BREAKING CHANGE! It is recommended that you have a body that " +
           "explains this BREAKING CHANGE.");
    }
    debug("checking null/empty description %s", description);
    if (Utils.isEmpty(description)) {
      exit("Commit message is not valid!\n\tWritten description is empty!\n\tYour commit message first line: `" + subjectLine + "`");
      return;
    }
    debug("checking null/empty description %s", description);
    if (description.endsWith(".")) {
      exit("Commit message is not valid!\n\tWritten description can not be ended with a `.`\n\tYour commit message first line: `" + subjectLine + "`");
      return;
    }
    debug("checking line-length of description %s", description);
    if (subjectLine.length() > 100) {
      exit("Commit message is not valid!\n\tSubject line size must not be larger than " +
           "100!(Yours is: " + subjectLine.length() + ")\n\tYour commit message first line: `" + subjectLine + "`");
      return;
    }
    if (splitStr.length > 1) {
      String newLineBeforeBody = splitStr[1];
      debug("calculate newLineBeforeBody %s", newLineBeforeBody);

      debug("checking newLineBeforeBody is just a new line: %s", newLineBeforeBody);
      if (null != newLineBeforeBody && !newLineBeforeBody.equals("")) {
        exit("You have entered text which should just be a new line!\n\tConventional " +
             "Commits' rules say that after the first line, you MUST enter a blank line before\n\tstarting " +
             "for your commit message body/footer! For Example:\n\tfeat: add hat wobble\n\t\t\t\t\t\t\t\t\t\t" +
             "<--empty line here\n\tSome details about " +
             "your commit:\n\tBreaking changes,\n\tissue mentions,\n\tacknowledgements\n\tetc...");
        return;
      }
    }
    if (splitStr.length > 2) {
      StringBuilder sb = new StringBuilder();
      for (int i = 2; i < splitStr.length; i++) {
        sb.append(splitStr[i]);
        if (i != splitStr.length - 1) {
          sb.append("\n");
        }
      }
      String body = sb.toString();
      debug("combine everything to variable `body`: %s", body);
    }
  }

  /**
   * log info.
   *
   * @param msg  msg
   * @param args args
   */
  protected void info(String msg, Object... args) {
    getLog().info(String.format(msg, args));
  }

  /**
   * log error.
   *
   * @param msg  msg
   * @param args args
   */
  protected void error(String msg, Object... args) {
    getLog().error(String.format(msg, args));
  }

  /**
   * log debug.
   *
   * @param msg  msg
   * @param args args
   */
  protected void debug(String msg, Object... args) {
    getLog().debug(String.format(msg, args));
  }

  /**
   * log warn.
   *
   * @param msg  msg
   * @param args args
   */
  protected void warn(String msg, Object... args) {
    getLog().warn(String.format(msg, args));
  }

  /**
   * exit failing if failOnError is true, log error otherwise.
   *
   * @param msg  msg
   * @param args args
   * @throws MojoFailureException given error message if failOnError setting is true.
   */
  protected void exit(String msg, Object... args) throws MojoFailureException {
    if (failOnError) {
      throw new MojoFailureException(String.format(msg, args));
    }
    error(msg, args);
  }

  /**
   * getter skip.
   *
   * @return boolean
   */
  public boolean isSkip() {
    return skip;
  }

  /**
   * setter skip.
   *
   * @param skip skip
   */
  public void setSkip(boolean skip) {
    this.skip = skip;
  }

  /**
   * getter failOnError.
   *
   * @return boolean
   */
  public boolean isFailOnError() {
    return failOnError;
  }

  /**
   * setter failOnError.
   *
   * @param failOnError failOnError
   */
  public void setFailOnError(boolean failOnError) {
    this.failOnError = failOnError;
  }
}
