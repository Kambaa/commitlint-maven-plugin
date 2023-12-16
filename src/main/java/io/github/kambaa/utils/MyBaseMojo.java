package io.github.kambaa.utils;

import static io.github.kambaa.utils.Utils.COMMIT_MESSAGE_SPLITTING_REGEX;
import static io.github.kambaa.utils.Utils.DEFAULT_COMMIT_MESSAGE_SUBJECT_TYPES;
import static io.github.kambaa.utils.Utils.isEmpty;

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
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Base mojo abstract class.
 */
public class MyBaseMojo extends AbstractMojo {
  /**
   * Constructor.
   */
  public MyBaseMojo() {
  }

  /**
   * Skips execution if true.
   */
  @Parameter(defaultValue = "false")
  protected boolean skip = false;

  /**
   * If sets to false, validations errors will be be a build failure, instead messages will be logged as an error.
   */
  @Parameter(defaultValue = "true")
  protected boolean failOnError = true;

  /**
   * Check maven command is given with -X debug option.
   */
  @Parameter(property = "maven.logging.debug")
  protected boolean mavenDebug;

  /**
   * Enable default message checking.
   */
  @Parameter(defaultValue = "true")
  protected boolean enableDefaultMessageChecking = true;

  /**
   * Enables usages of default commit message subject types. Which are:
   * build, chore, ci, docs, feat, fix, perf, refactor, revert, style, test.
   */
  @Parameter(defaultValue = "true")
  protected boolean enableDefaultCommitSubjectTypes = true;

  /**
   * Enable forcing `subject line must not end with .` checks.
   * <br>For example:<br>
   * feat: add hat wobble. -- notice the dot at the end.<br>
   * If set to true, it will cause error and quit if this case is found.<br>
   * If set to false, only a warning will be generated if this case is found.
   * Default is false.
   */
  @Parameter(defaultValue = "true")
  protected boolean enableForcingSubjectEndsWithCommaErrorCheck = true;

  /**
   * Enable forcing `an empty new line must be between subject and body.` checks.
   * <br>For example:<br>
   * feat: add hat wobble.<br> -- notice the empty line here<br><br>this is commit body<br>
   * If set to true, it will cause error and quit if this case is found.<br>
   * If set to false, only a warning will be generated if this case is found.
   * Default is false.
   */
  @Parameter(defaultValue = "false")
  protected boolean enableForcingNewLineBetweenSubjectAndBodyCheck = false;

  /**
   * Add custom commit message subject types.
   * These types will be added to the default subject types
   * if `enableDefaultCommitSubjectTypes` is `true`. If you want to
   * run only custom defined  message subject types to check from,
   * set `enableDefaultCommitSubjectTypes` to `false`.
   */
  @Parameter(property = "subjectType")
  protected List<String> customCommitSubjectTypes;

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
  protected List<String> customCheckingMethods;

  /**
   * Enables default ignore patterns. Default is true.
   */
  @Parameter(defaultValue = "true")
  protected boolean enableDefaultIgnorePatterns = true;

  /**
   * Add custom ignore patterns.
   * These patterns will be added to the default ignore patterns
   * if `enableDefaultIgnorePatterns` is `true`. If you want to
   * run only custom defined ignore patterns,
   * set `enableDefaultIgnorePatterns` to `false`.
   */
  @Parameter(property = "pattern")
  protected List<String> customIgnorePatterns;

  /**
   * Separator used in the git log command.
   * Default is Utils.DEFAULT_SEPARATOR
   */
  @Parameter(defaultValue = Utils.DEFAULT_SEPARATOR)
  protected String separator = Utils.DEFAULT_SEPARATOR;

  /**
   * Ignore commit message patterns list.
   * Both initial and custom patterns will be filled here upon execution.
   */
  protected List<Pattern> ignorePatterns;

  /**
   * Subject types list for checking.
   * Both initial and custom types will be filled here upon execution.
   */
  protected Set<String> subjectTypeList;

  /**
   * Common mojo execute method, should be called on the child classes.
   */
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

  /**
   * Splits given commit messages (generated via the git log command) by the given separator.
   *
   * @param gitLogMessages String value of git log --format='%B%n{GIVEN_SEPARATOR}'.
   * @param separator      separator used on the git log command.
   * @return List of strings that each one represents one of the commit messages.
   * @throws MojoFailureException err
   */
  protected List<String> splitCommitMessagesFromGivenLogString(String gitLogMessages, String separator) throws MojoFailureException {
    if (gitLogMessages.contains("\r\n")) {
      debug("Given text contains `\\r\\n`, first replacing them with `\\n`");
      gitLogMessages = gitLogMessages.replace("\r\n", "\n");
    }
    if (!gitLogMessages.contains(separator)) {
      debug("Given file does not contain any separator string!");
      throw new MojoFailureException("Given file does not contain separator text! are you sure you are giving the `git log` with separator command result?");
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

  protected List<String> filterOutIgnoredCommits(List<String> commitMessagesToCheck) {
    List<String> out = commitMessagesToCheck.stream()
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
   * Checks commit message.
   *
   * @param commitMessage given commit message text.
   * @throws MojoFailureException if any problems occur.
   */
  protected void checkCommitMessage(String commitMessage) throws MojoFailureException {
    debug("Commit checking started for: %s", commitMessage);
    if (isEmpty(commitMessage)) {
      debug("Commit message is null or empty, quitting");
      throw new MojoFailureException("Commit message can not be null or empty!");
    }

    String[] splitStr = commitMessage.split("\n");
    // todo: remove this if becaouse isEmpty check covers this.
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
        String str = "You have entered text which should just be a new line! Conventional " +
                     "Commits' rules say that after the first line, you MUST enter a blank line first! For Example:\nfeat: add hat wobble\n" +
                     "\t\t\t---empty line here\nSome details about " +
                     "your commit:\nBreaking changes,\nissue mentions,\nacknowledgements\netc...";
        if (enableForcingNewLineBetweenSubjectAndBodyCheck) {
          throw new MojoFailureException(str);
        } else {
          warn(str);
        }
      }
    }
  }

  /**
   * addInitialSubjectTypeList.
   */
  protected void addInitialSubjectTypeList() {
    if (enableDefaultCommitSubjectTypes) {
      subjectTypeList.addAll(DEFAULT_COMMIT_MESSAGE_SUBJECT_TYPES);
    }
  }

  /**
   * addCustomCommitSubjectTypesIfGiven.
   */
  protected void addCustomCommitSubjectTypesIfGiven() {
    if (!isEmpty(customCommitSubjectTypes)) {
      final Set<String> customSubjectTypes = new HashSet<>(customCommitSubjectTypes);
      subjectTypeList.addAll(customSubjectTypes);
    }
  }

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

  /**
   * isMavenDebug.
   *
   * @return mavenDebug
   */
  public boolean isMavenDebug() {
    return mavenDebug;
  }

  /**
   * setMavenDebug.
   *
   * @param mavenDebug mavenDebug
   */
  public void setMavenDebug(boolean mavenDebug) {
    this.mavenDebug = mavenDebug;
  }

  /**
   * isEnableDefaultIgnorePatterns.
   *
   * @return enableDefaultIgnorePatterns
   */
  public boolean isEnableDefaultIgnorePatterns() {
    return enableDefaultIgnorePatterns;
  }

  /**
   * setEnableDefaultIgnorePatterns.
   *
   * @param enableDefaultIgnorePatterns enableDefaultIgnorePatterns
   */
  public void setEnableDefaultIgnorePatterns(boolean enableDefaultIgnorePatterns) {
    this.enableDefaultIgnorePatterns = enableDefaultIgnorePatterns;
  }

  /**
   * getCustomIgnorePatterns.
   *
   * @return customIgnorePatterns
   */
  public List<String> getCustomIgnorePatterns() {
    return customIgnorePatterns;
  }

  /**
   * setCustomIgnorePatterns.
   *
   * @param customIgnorePatterns customIgnorePatterns
   */
  public void setCustomIgnorePatterns(List<String> customIgnorePatterns) {
    this.customIgnorePatterns = customIgnorePatterns;
  }

  /**
   * isEnableDefaultMessageChecking.
   *
   * @return enableDefaultMessageChecking
   */
  public boolean isEnableDefaultMessageChecking() {
    return enableDefaultMessageChecking;
  }

  /**
   * setEnableDefaultMessageChecking.
   *
   * @param enableDefaultMessageChecking enableDefaultMessageChecking
   */
  public void setEnableDefaultMessageChecking(boolean enableDefaultMessageChecking) {
    this.enableDefaultMessageChecking = enableDefaultMessageChecking;
  }

  /**
   * getCustomCheckingMethods.
   *
   * @return customCheckingMethods
   */
  public List<String> getCustomCheckingMethods() {
    return customCheckingMethods;
  }

  /**
   * setCustomCheckingMethods.
   *
   * @param customCheckingMethods customCheckingMethods
   */
  public void setCustomCheckingMethods(List<String> customCheckingMethods) {
    this.customCheckingMethods = customCheckingMethods;
  }

  /**
   * isEnableDefaultCommitSubjectTypes.
   *
   * @return enableDefaultCommitSubjectTypes
   */
  public boolean isEnableDefaultCommitSubjectTypes() {
    return enableDefaultCommitSubjectTypes;
  }

  /**
   * setEnableDefaultCommitSubjectTypes.
   *
   * @param enableDefaultCommitSubjectTypes enableDefaultCommitSubjectTypes
   */
  public void setEnableDefaultCommitSubjectTypes(boolean enableDefaultCommitSubjectTypes) {
    this.enableDefaultCommitSubjectTypes = enableDefaultCommitSubjectTypes;
  }

  /**
   * getCustomCommitSubjectTypes.
   *
   * @return customCommitSubjectTypes
   */
  public List<String> getCustomCommitSubjectTypes() {
    return customCommitSubjectTypes;
  }

  /**
   * setCustomCommitSubjectTypes.
   *
   * @param customCommitSubjectTypes customCommitSubjectTypes
   */
  public void setCustomCommitSubjectTypes(List<String> customCommitSubjectTypes) {
    this.customCommitSubjectTypes = customCommitSubjectTypes;
  }

  /**
   * isEnableForcingSubjectEndsWithCommaErrorCheck.
   *
   * @return enableForcingSubjectEndsWithCommaErrorCheck
   */
  public boolean isEnableForcingSubjectEndsWithCommaErrorCheck() {
    return enableForcingSubjectEndsWithCommaErrorCheck;
  }

  /**
   * setEnableForcingSubjectEndsWithCommaErrorCheck.
   *
   * @param enableForcingSubjectEndsWithCommaErrorCheck enableForcingSubjectEndsWithCommaErrorCheck
   */
  public void setEnableForcingSubjectEndsWithCommaErrorCheck(boolean enableForcingSubjectEndsWithCommaErrorCheck) {
    this.enableForcingSubjectEndsWithCommaErrorCheck = enableForcingSubjectEndsWithCommaErrorCheck;
  }

  /**
   * isEnableForcingNewLineBetweenSubjectAndBodyCheck.
   *
   * @return enableForcingNewLineBetweenSubjectAndBodyCheck
   */
  public boolean isEnableForcingNewLineBetweenSubjectAndBodyCheck() {
    return enableForcingNewLineBetweenSubjectAndBodyCheck;
  }

  /**
   * setEnableForcingNewLineBetweenSubjectAndBodyCheck.
   *
   * @param enableForcingNewLineBetweenSubjectAndBodyCheck enableForcingNewLineBetweenSubjectAndBodyCheck
   */
  public void setEnableForcingNewLineBetweenSubjectAndBodyCheck(boolean enableForcingNewLineBetweenSubjectAndBodyCheck) {
    this.enableForcingNewLineBetweenSubjectAndBodyCheck = enableForcingNewLineBetweenSubjectAndBodyCheck;
  }

  /**
   * getIgnorePatterns.
   *
   * @return ignorePatterns
   */
  public List<Pattern> getIgnorePatterns() {
    return ignorePatterns;
  }

  /**
   * getSubjectTypeList.
   *
   * @return subjectTypeList
   */
  public Set<String> getSubjectTypeList() {
    return subjectTypeList;
  }

}
