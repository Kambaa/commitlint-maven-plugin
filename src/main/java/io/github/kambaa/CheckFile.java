package io.github.kambaa;

import static io.github.kambaa.utils.Utils.isEmpty;

import io.github.kambaa.utils.MyBaseMojo;
import io.github.kambaa.utils.Utils;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Checks commit message.
 */
@Mojo(name = "checkFile")
public class CheckFile extends MyBaseMojo {

  /**
   * File name containing the commit messages.
   * File must be generated via `git log` command and placed within the project folder
   */
  @Parameter(required = true)
  private String file;

  /**
   * Separator used in the git log command.
   * Default is {@link Utils.DEFAULT_SEPARATOR}
   */
  @Parameter(defaultValue = Utils.DEFAULT_SEPARATOR)
  private String separator = Utils.DEFAULT_SEPARATOR;

  /**
   * Enables default ignore patterns. Default is true.
   */
  @Parameter(defaultValue = "true")
  private boolean enableDefaultIgnorePatterns = true;

  /**
   * Add custom ignore patterns.
   * These patterns will be added to the default ignore patterns
   * if `enableDefaultIgnorePatterns` is `true`. If you want to
   * run only custom defined ignore patterns,
   * set `enableDefaultIgnorePatterns` to `false`.
   */
  @Parameter(property = "pattern")
  private List<String> customIgnorePatterns;

  /**
   * Raw Commit message list extracted from given file.
   */
  private List<String> commitMessageList;

  /**
   * Filtered out commit message list.
   */
  private List<String> ignoreFilteredCommitMessageList;

  /**
   * Ignore commit message patterns list.
   * Both initial and custom patterns will be filled here upon execution.
   */
  private List<Pattern> ignorePatterns;

  @Override
  public void execute() throws MojoFailureException {
    ignorePatterns = new ArrayList<>();
    addInitialIgnorePatterns();
    addCustomIgnorePatternsIfGiven();

    commitMessageList = splitCommitMessagesFromGitLog(checkAndReadGivenFile(), separator);
    debug("Extracted commit messages: ");
    debugLogCommitMessageList(commitMessageList);
    ignoreFilteredCommitMessageList = filterOutIgnoredCommits(commitMessageList);
    debug("Remaining commit messages(filtered out ignored commits): ");
    debugLogCommitMessageList(ignoreFilteredCommitMessageList);
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

  private void debugLogCommitMessageList(List<String> commitMessageList) {
    if (mavenDebug) {
      debug("%d commits", commitMessageList.size());
      commitMessageList.forEach(s -> {
        debug("\t%s", s);
      });
    }
  }

  /**
   * Splits given commit messages (generated via the git log command) by the given separator.
   *
   * @param gitLogMessages String value of git log --format='%B%n{GIVEN_SEPARATOR}'.
   * @param separator      separator used on the git log command.
   * @return List of strings that each one represents one of the commit messages.
   */
  private List<String> splitCommitMessagesFromGitLog(String gitLogMessages, String separator) throws MojoFailureException {
    if (!gitLogMessages.contains(separator)) {
      debug("Given file does not contain any separator string!");
      throw new MojoFailureException("Given file does not contain separator text are you sure you are giving the `git log` with separator command result?");
    }
    String[] commitMsgArr = gitLogMessages.split(separator);
    if (isEmpty(commitMsgArr)) {
      debug("Seperation result is empty, returning null!\n\tSeparator: %s\n\tGitLogMessages: %s ", separator, gitLogMessages);
      return null;
    }
    List<String> out = Stream.of(commitMsgArr).map(s -> {
      // Trim new lines at the beginning and at the end of each splitted commit message(which is written by git log command.
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

  private void addCustomIgnorePatternsIfGiven() throws MojoFailureException {
    if (!isEmpty(customIgnorePatterns)) {
      final List<String> invalidPatterns = new ArrayList<>();
      for (String customPatternString : customIgnorePatterns) {
        try {
          Pattern customPattern = Pattern.compile(customPatternString);
          ignorePatterns.add(customPattern);
        } catch (PatternSyntaxException exception) {
          debug("Custom ignore pattern error on %s: ", customPatternString);
          if (mavenDebug) {
            exception.printStackTrace();
          }
          invalidPatterns.add(customPatternString + "->" + exception.getMessage());
        }
      }
      if (!isEmpty(invalidPatterns)) {
        throw new MojoFailureException("There are errors on given custom ignore patters. "
                                       + "Please fix them and run again!\n\t"
                                       + String.join("\n\t", invalidPatterns));
      }
    }
  }

  private void addInitialIgnorePatterns() {
    if (enableDefaultIgnorePatterns) {
      ignorePatterns.add(Pattern.compile("^((Merge pull request)|(Merge (.*?) into (.*?)|(Merge branch (.*?)))(?:\\r?\\n)*$)", Pattern.MULTILINE));
      ignorePatterns.add(Pattern.compile("^(Merge tag (.*?))(?:\\r?\\n)*$", Pattern.MULTILINE));
      ignorePatterns.add(Pattern.compile("^(R|r)evert (.*)"));
      ignorePatterns.add(Pattern.compile("^(fixup|squash)!"));
      ignorePatterns.add(Pattern.compile("^(Merged (.*?)(in|into) (.*)|Merged PR (.*): (.*))"));
      ignorePatterns.add(Pattern.compile("^Merge remote-tracking branch(\\s*)(.*)"));
      ignorePatterns.add(Pattern.compile("^Automatic merge(.*)"));
      ignorePatterns.add(Pattern.compile("^Auto-merged (.*?) into (.*)"));
      ignorePatterns.add(Pattern.compile("^(C|c)hore(\\([^)]+\\))?:"));
    }
  }

  private void filterIgnoredCommitMessagePatterns() {

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

  public boolean isEnableDefaultIgnorePatterns() {
    return enableDefaultIgnorePatterns;
  }

  public void setEnableDefaultIgnorePatterns(boolean enableDefaultIgnorePatterns) {
    this.enableDefaultIgnorePatterns = enableDefaultIgnorePatterns;
  }

  public List<String> getCustomIgnorePatterns() {
    return customIgnorePatterns;
  }

  public void setCustomIgnorePatterns(List<String> customIgnorePatterns) {
    this.customIgnorePatterns = customIgnorePatterns;
  }

  /**
   * Constructor.
   */
  public CheckFile() {
  }

  public List<String> getCommitMessageList() {
    return commitMessageList;
  }

  public List<Pattern> getIgnorePatterns() {
    return ignorePatterns;
  }

  public List<String> getIgnoreFilteredCommitMessageList() {
    return ignoreFilteredCommitMessageList;
  }

}
