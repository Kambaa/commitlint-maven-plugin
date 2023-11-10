package io.github.kambaa;

import static io.github.kambaa.utils.Utils.COMMIT_MESSAGE_SPLITTING_REGEX;
import static io.github.kambaa.utils.Utils.DEFAULT_COMMIT_MESSAGE_SUBJECT_TYPES;
import static io.github.kambaa.utils.Utils.isEmpty;

import io.github.kambaa.utils.MyBaseMojo;
import io.github.kambaa.utils.Utils;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
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
   * Enables usages of default commit message subject types. Which are:
   * build, chore, ci, docs, feat, fix, perf, refactor, revert, style, test.
   */
  @Parameter(defaultValue = "true")
  private boolean enableDefaultCommitSubjectTypes = true;

  /**
   * Enable forcing `subject line must not end with .` checks.
   * <br>For example:<br>
   * feat: add hat wobble. <-- notice the dot at the end.<br>
   * If set to true, it will cause error and quit if this case is found.<br>
   * If set to false, only a warning will be generated if this case is found.
   * Default is false.
   */
  @Parameter(defaultValue = "true")
  private boolean enableForcingSubjectEndsWithCommaErrorCheck = true;

  /**
   * Enable forcing `an empty new line must be between subject and body.` checks.
   * <br>For example:<br>
   * feat: add hat wobble.<br> <-- notice the empty line here<br><br>this is commit body<br>
   * If set to true, it will cause error and quit if this case is found.<br>
   * If set to false, only a warning will be generated if this case is found.
   * Default is false.
   */
  @Parameter(defaultValue = "false")
  private boolean enableForcingNewLineBetweenSubjectAndBodyCheck = false;

  /**
   * Add custom commit message subject types.
   * These types will be added to the default subject types
   * if `enableDefaultCommitSubjectTypes` is `true`. If you want to
   * run only custom defined  message subject types to check from,
   * set `enableDefaultCommitSubjectTypes` to `false`.
   */
  @Parameter(property = "subjectType")
  private List<String> customCommitSubjectTypes;

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

  /**
   * Subject types list for checking.
   * Both initial and custom types will be filled here upon execution.
   */
  private Set<String> subjectTypeList;

  // @Parameter(defaultValue = "${project}", required = true, readonly = true)
  // private MavenProject project;
  // /**
  //  * Custom class loader for custom message checking methods access.
  //  */
  // private ClassLoader customClassLoader;

  // private ClassLoader getClassLoader() {
  //   ClassLoader classLoader = null;
  //   try {
  //     List<String> classpathElements = project.getRuntimeClasspathElements();
  //     if (null == classpathElements) {
  //       return Thread.currentThread().getContextClassLoader();
  //     }
  //     URL[] urls = new URL[classpathElements.size()];
  //
  //     for (int i = 0; i < classpathElements.size(); ++i) {
  //       urls[i] = new File((String) classpathElements.get(i)).toURI().toURL();
  //     }
  //     classLoader = new URLClassLoader(urls, getClass().getClassLoader());
  //   } catch (Exception e) {
  //     e.printStackTrace();
  //   }
  //   return classLoader;
  // }

  @Override
  public void execute() throws MojoFailureException {
    ignorePatterns = new ArrayList<>();
    addInitialIgnorePatterns();
    addCustomIgnorePatternsIfGiven();

    subjectTypeList = new HashSet<>();
    addInitialSubjectTypeList();
    addCustomCommitSubjectTypesIfGiven();

    if (!isEmpty(customCheckingMethods)) {
      final List<String> invalidPatterns = new ArrayList<>();
      // customClassLoader = getClassLoader();
      for (String customMethodString : customCheckingMethods) {
        String className = customMethodString.substring(0, customMethodString.lastIndexOf("."));
        String methodName = customMethodString.substring(customMethodString.lastIndexOf(".") + 1);
        // customClassLoader.loadClass(validationConfig.getClassName());
        // Method method = clazz.getDeclaredMethod(methodName, String.class);
      }
    }

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

  /**
   * Checks commit message.
   *
   * @param commitMessage given commit message text.
   * @throws MojoFailureException if any problems occur.
   */
  private void checkCommitMessage(String commitMessage) throws MojoFailureException {
    debug("Commit checking started for: %s", commitMessage);
    if (isEmpty(commitMessage)) {
      debug("Commit message is null or empty, quitting");
      throw new MojoFailureException("Commit message can not be null or empty!");
    }

    String[] splitStr = commitMessage.split("\n");
    if (splitStr.length == 0) {
      throw new MojoFailureException(String.format("Commit message can not be parsed! Commit message: %s", commitMessage));
    }
    debug("Capture Group Count: %d", splitStr.length);

    String subjectLine = splitStr[0];
    debug("Extracted subject is: %s", subjectLine);

    Pattern pattern = Pattern.compile(COMMIT_MESSAGE_SPLITTING_REGEX);
    Matcher subjectMatcher = pattern.matcher(subjectLine);
    if (!subjectMatcher.matches()) {
      debug("Commit message can not be parsed according to the pattern: %s", pattern.pattern());
      debug("Commit Message is: %s", commitMessage);
      debug("Subject Line is: %s", subjectLine);
      throw new MojoFailureException(
          String.format("Commit message can not be parsed!\nStart by entering a commit message " +
                        "like this:%nfeat: add hat wobble\nYour commit message's subject(first line): `%s`%nCommit message is: `%s`", subjectLine, commitMessage));
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

    checkValidGivenSubjectTypes(commitMessage, type);

    debug("checking scope: %s", scope);
    if (null != scope) {
      info("You have a scope on your commit message!");
    }

    debug("checking breaking change indicator: %s", breakingChangeIndicator);
    if (null != breakingChangeIndicator) {
      info("You have an indicator for a BREAKING CHANGE! It is recommended that you have a body that " +
           "explains this BREAKING CHANGE.");
    }

    checkNullOrEmptyDescription(subjectLine, description);

    checkSubjectLineEndsWithPeriod(subjectLine, description);

    checkSubjectLineLength(subjectLine);

    checkCommitMessageBodyStartsWithNewLine(splitStr);
  }

  private void checkValidGivenSubjectTypes(String commitMessage, String type) throws MojoFailureException {
    String typeNamesForLogging = "`" + String.join("`,`", subjectTypeList.toArray(new String[0])) + "`";
    debug("checking written type name: `%s` is one of %s", type, typeNamesForLogging);
    if (!subjectTypeList.contains(type)) {
      throw new MojoFailureException(
          String.format("Commit message is not valid!%nWritten type(`%s`) is not one of: %s%nCommit message is: %s",
              type, typeNamesForLogging, commitMessage));
    }
  }

  private void checkNullOrEmptyDescription(String subjectLine, String description) throws MojoFailureException {
    debug("checking null/empty description: %s", description);
    if (Utils.isEmpty(description)) {
      throw new MojoFailureException(
          String.format("Commit message is not valid!%nNo description found on commit message subject(first line)!%nYour commit message subject(first line): `%s`", subjectLine));
    }
  }

  private void checkSubjectLineEndsWithPeriod(String subjectLine, String description) throws MojoFailureException {
    debug("checking ending with `.` on subject description: %s", description);
    if (description.endsWith(".")) {
      if (enableForcingSubjectEndsWithCommaErrorCheck) {
        throw new MojoFailureException(
            String.format("Commit message is not valid!%nYour commit message subject(first line) description must not end with a `.`%nYour commit message subject(first line): `%s`",
                subjectLine));
      } else {
        warn("Your commit message subject(first line) description must not end with a `.`%nYour commit message subject(first line): `%s`", subjectLine);
      }
    }
  }

  private void checkSubjectLineLength(String subjectLine) throws MojoFailureException {
    debug("checking line-length of subject:", subjectLine);
    if (subjectLine.length() > 100) {
      throw new MojoFailureException(String.format("Commit message is not valid!\n\tSubject line size must not be larger than " +
                                                   "100!(Yours is: %d)\nYour commit message subject(first line): `%s", subjectLine.length(), subjectLine));
    }
  }

  private void checkCommitMessageBodyStartsWithNewLine(String[] splitStr) throws MojoFailureException {
    if (splitStr.length > 1) {
      String newLineBeforeBody = splitStr[1];
      debug("calculate newLineBeforeBody: %s", newLineBeforeBody);

      debug("checking newLineBeforeBody is just a new line: %s", newLineBeforeBody);
      if (null != newLineBeforeBody && !newLineBeforeBody.equals("")) {
        String str = "You have entered text which should just be a new line!\nConventional " +
                     "Commits' rules say that after the first line, you MUST enter a blank line before\nstarting " +
                     "for your commit message body/footer! For Example:\nfeat: add hat wobble\n" +
                     "\t\t\t<--empty line here\nSome details about " +
                     "your commit:\nBreaking changes,\nissue mentions,\nacknowledgements\netc...";
        if (enableForcingNewLineBetweenSubjectAndBodyCheck) {
          throw new MojoFailureException(str);
        } else {
          warn(str);
        }
      }
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
      ignorePatterns.add(Pattern.compile("^([Rr])evert (.*)"));
      ignorePatterns.add(Pattern.compile("^(fixup|squash)!"));
      ignorePatterns.add(Pattern.compile("^(Merged (.*?)(in|into) (.*)|Merged PR (.*): (.*))"));
      ignorePatterns.add(Pattern.compile("^Merge remote-tracking branch(\\s*)(.*)"));
      ignorePatterns.add(Pattern.compile("^Automatic merge(.*)"));
      ignorePatterns.add(Pattern.compile("^Auto-merged (.*?) into (.*)"));
      ignorePatterns.add(Pattern.compile("^([Cc])hore(\\([^)]+\\))?:"));
    }
  }

  private void addInitialSubjectTypeList() {
    if (enableDefaultCommitSubjectTypes) {
      subjectTypeList.addAll(DEFAULT_COMMIT_MESSAGE_SUBJECT_TYPES);
    }
  }

  private void addCustomCommitSubjectTypesIfGiven() {
    if (!isEmpty(customCommitSubjectTypes)) {
      final Set<String> customSubjectTypes = new HashSet<>(customCommitSubjectTypes);
      subjectTypeList.addAll(customSubjectTypes);
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

  public boolean isEnableDefaultCommitSubjectTypes() {
    return enableDefaultCommitSubjectTypes;
  }

  public void setEnableDefaultCommitSubjectTypes(boolean enableDefaultCommitSubjectTypes) {
    this.enableDefaultCommitSubjectTypes = enableDefaultCommitSubjectTypes;
  }

  public List<String> getCustomCommitSubjectTypes() {
    return customCommitSubjectTypes;
  }

  public void setCustomCommitSubjectTypes(List<String> customCommitSubjectTypes) {
    this.customCommitSubjectTypes = customCommitSubjectTypes;
  }

  public Set<String> getSubjectTypeList() {
    return subjectTypeList;
  }

  public boolean isEnableForcingSubjectEndsWithCommaErrorCheck() {
    return enableForcingSubjectEndsWithCommaErrorCheck;
  }

  public void setEnableForcingSubjectEndsWithCommaErrorCheck(boolean enableForcingSubjectEndsWithCommaErrorCheck) {
    this.enableForcingSubjectEndsWithCommaErrorCheck = enableForcingSubjectEndsWithCommaErrorCheck;
  }

  public boolean isEnableForcingNewLineBetweenSubjectAndBodyCheck() {
    return enableForcingNewLineBetweenSubjectAndBodyCheck;
  }

  public void setEnableForcingNewLineBetweenSubjectAndBodyCheck(boolean enableForcingNewLineBetweenSubjectAndBodyCheck) {
    this.enableForcingNewLineBetweenSubjectAndBodyCheck = enableForcingNewLineBetweenSubjectAndBodyCheck;
  }
}
