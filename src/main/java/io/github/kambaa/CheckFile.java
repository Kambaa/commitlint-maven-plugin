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
   * Enable default message checking.
   */
  @Parameter(defaultValue = "true")
  private boolean enableDefaultMessageChecking = true;

  /**
   * Custom commit message checking static methods.
   * i.e:
   * com.example.MyClass.myCustomCommitMessageCheck
   * Your custom checking method must be like this:
   * public static boolean myCustomCommitMessageCheck(String commitMessage)
   * These methods will be added to the default message checking
   * if `enableDefaultMessageChecking` is `true`. If you want to
   * run only custom defined message checks,
   * set `enableDefaultMessageChecking` to `false`.
   */
  @Parameter(property = "method")
  private List<String> customCheckingMethods;

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
    debug("Extracted %d commits:\n--> %s", commitMessageList.size(),
        String.join("\n--> ", commitMessageList));
    ignoreFilteredCommitMessageList = filterOutIgnoredCommits(commitMessageList);
    debug("Remaining %d commit messages(filtered out ignored commits):\n--> %s",
        ignoreFilteredCommitMessageList.size(),
        String.join("\n--> ", ignoreFilteredCommitMessageList)
    );

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
    gitLogMessages = gitLogMessages.replaceAll("\r\n", "\n");
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

  // /**
  //  * checks commit message.
  //  *
  //  * @param testCommitMessage given commit message text.
  //  * @throws MojoFailureException if any problems occur.
  //  */
  // protected void checkCommitMsg(String testCommitMessage) throws MojoFailureException {
  //   String[] splitStr = testCommitMessage.split("\n");
  //   if (splitStr.length == 0) {
  //     exit("Commit message can not be parsed!");
  //   }
  //   debug("Capture Group Count: %d", splitStr.length);
  //
  //   String subjectLine = splitStr[0];
  //   debug("Extracted subject is: %s", subjectLine);
  //   Pattern pattern = Pattern.compile("^(\\w*)(?:\\(([\\w\\-.]+)\\))?(!)?: (.*)(\\n[\\s\\S]*)?");
  //   Matcher subjectMatcher = pattern.matcher(subjectLine);
  //   if (!subjectMatcher.matches()) {
  //     debug("Are you adding new lines before commit?");
  //     debug("Subject Line is: %s", subjectLine);
  //     debug("Commit Message is: %s", testCommitMessage);
  //     exit("Commit message can not be parsed!\n\tStart by entering a commit message " +
  //          "like this:\n\tfeat: add hat wobble\n\tYour commit message first line: `" + subjectLine + "`\n\tCommit message is: `" + testCommitMessage + "`");
  //   }
  //
  //   String type = subjectMatcher.group(1);
  //   String scope = subjectMatcher.group(2);
  //   String breakingChangeIndicator = subjectMatcher.group(3);
  //   String description = subjectMatcher.group(4);
  //   debug("Extracted info from capture groups: ");
  //   debug("type: %s", type);
  //   debug("scope: %s", scope);
  //   debug("indicator for a breaking change: %s", breakingChangeIndicator);
  //   debug("description: %s", description);
  //
  //   Set<String> types = new HashSet<>(Arrays.asList(
  //       "build",
  //       "chore",
  //       "ci",
  //       "docs",
  //       "feat",
  //       "fix",
  //       "perf",
  //       "refactor",
  //       "revert",
  //       "style",
  //       "test"));
  //   debug("checking type name: `%s`", type);
  //
  //   if (!types.contains(type)) {
  //     exit("Commit message is not valid!\n\tWritten type(`" + type + "`) is not one of: " +
  //          "`" + String.join("`,`", types.toArray(new String[0])) + "`");
  //     return;
  //   }
  //   debug("done checking type name: %s", types);
  //   debug("checking scope %s", scope);
  //   if (null != scope) {
  //     warn("You have a scope on your commit message!");
  //   }
  //   debug("checking breakingChangeIndicator %s", breakingChangeIndicator);
  //   if (null != breakingChangeIndicator) {
  //     warn("You have an indicator for a BREAKING CHANGE! It is recommended that you have a body that " +
  //          "explains this BREAKING CHANGE.");
  //   }
  //   debug("checking null/empty description %s", description);
  //   if (Utils.isEmpty(description)) {
  //     exit("Commit message is not valid!\n\tWritten description is empty!\n\tYour commit message first line: `" + subjectLine + "`");
  //     return;
  //   }
  //   debug("checking null/empty description %s", description);
  //   if (description.endsWith(".")) {
  //     exit("Commit message is not valid!\n\tWritten description can not be ended with a `.`\n\tYour commit message first line: `" + subjectLine + "`");
  //     return;
  //   }
  //   debug("checking line-length of description %s", description);
  //   if (subjectLine.length() > 100) {
  //     exit("Commit message is not valid!\n\tSubject line size must not be larger than " +
  //          "100!(Yours is: " + subjectLine.length() + ")\n\tYour commit message first line: `" + subjectLine + "`");
  //     return;
  //   }
  //   if (splitStr.length > 1) {
  //     String newLineBeforeBody = splitStr[1];
  //     debug("calculate newLineBeforeBody %s", newLineBeforeBody);
  //
  //     debug("checking newLineBeforeBody is just a new line: %s", newLineBeforeBody);
  //     if (null != newLineBeforeBody && !newLineBeforeBody.equals("")) {
  //       exit("You have entered text which should just be a new line!\n\tConventional " +
  //            "Commits' rules say that after the first line, you MUST enter a blank line before\n\tstarting " +
  //            "for your commit message body/footer! For Example:\n\tfeat: add hat wobble\n\t\t\t\t\t\t\t\t\t\t" +
  //            "<--empty line here\n\tSome details about " +
  //            "your commit:\n\tBreaking changes,\n\tissue mentions,\n\tacknowledgements\n\tetc...");
  //       return;
  //     }
  //   }
  //   if (splitStr.length > 2) {
  //     StringBuilder sb = new StringBuilder();
  //     for (int i = 2; i < splitStr.length; i++) {
  //       sb.append(splitStr[i]);
  //       if (i != splitStr.length - 1) {
  //         sb.append("\n");
  //       }
  //     }
  //     String body = sb.toString();
  //     debug("combine everything to variable `body`: %s", body);
  //   }
  // }

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

  public boolean isEnableDefaultMessageChecking() {
    return enableDefaultMessageChecking;
  }

  public void setEnableDefaultMessageChecking(boolean enableDefaultMessageChecking) {
    this.enableDefaultMessageChecking = enableDefaultMessageChecking;
  }

  public List<String> getCustomCheckingMethods() {
    return customCheckingMethods;
  }

  public void setCustomCheckingMethods(List<String> customCheckingMethods) {
    this.customCheckingMethods = customCheckingMethods;
  }
}
