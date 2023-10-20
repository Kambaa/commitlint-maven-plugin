package io.github.kambaa;

import io.github.kambaa.utils.MyBaseMojo;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

/**
 * Checks commit message.
 */
@Mojo(name = "getDelimiter")
public class GetDelimiter extends MyBaseMojo {
  /**
   * Constructor.
   */
  public GetDelimiter() {
  }

  private String testCommitMessage;

  /**
   * Default splitting text(without quotes).
   * "\n------------------------ &gt;8 ------------------------\n"
   */
  public static final String DEFAULT_SEPARATOR = "\n------------------------ >8 ------------------------\n";

  List<String> commitMessageList;
  List<Pattern> ignorePatterns;

  @Override
  public void execute() throws MojoFailureException {
    // getLog().info(DEFAULT_SEPARATOR);
    // getLog().info(testCommitMessage);
    commitMessageList = splitCommitMessagesFromGitLog(testCommitMessage, DEFAULT_SEPARATOR);
    ignorePatterns = new ArrayList<>();
    ignorePatterns.add(Pattern.compile("^((Merge pull request)|(Merge (.*?) into (.*?)|(Merge branch (.*?)))(?:\\r?\\n)*$)", Pattern.MULTILINE));
    ignorePatterns.add(Pattern.compile("^(Merge tag (.*?))(?:\\r?\\n)*$", Pattern.MULTILINE));
    ignorePatterns.add(Pattern.compile("^(R|r)evert (.*)"));
    ignorePatterns.add(Pattern.compile("^(fixup|squash)!"));
    ignorePatterns.add(Pattern.compile("^(Merged (.*?)(in|into) (.*)|Merged PR (.*): (.*))"));
    ignorePatterns.add(Pattern.compile("^Merge remote-tracking branch(\\s*)(.*)"));
    ignorePatterns.add(Pattern.compile("^Automatic merge(.*)"));
    ignorePatterns.add(Pattern.compile("^Auto-merged (.*?) into (.*)"));
    ignorePatterns.add(Pattern.compile("^chore(\\([^)]+\\))?:"));

    // String s = "Merge pull request #1 from Kambaa/circleci-project-setup\n"
    //            + "CircleCI Commit:";
    // error(""+ignorePatterns.get(0).matcher(s).find());

    // commitMessageList.forEach(s -> {
    //   Boolean anyMatchResult = ignorePatterns.stream().anyMatch(pattern -> pattern.matcher(s).find());
    //   info("\n\t\tIGNORE PATTERN CHECK:\n\t\t\t%s:\n\t\t\t%s\n\n", s, anyMatchResult);
    // });

    List<String> commitMessagesToCheck = commitMessageList.stream()
        .filter(s -> ignorePatterns.stream()
            .noneMatch(pattern -> pattern.matcher(s).find())
        )
        .collect(Collectors.toList());

    info("commitMessagesToCheck:");
    commitMessagesToCheck.forEach(s -> {
      info("\n\t\t\t%s", s);
    });
  }

  private void filterIgnoredCommitMessagePatterns() {

  }

  public String getTestCommitMessage() {
    return testCommitMessage;
  }

  public void setTestCommitMessage(String testCommitMessage) {
    this.testCommitMessage = testCommitMessage;
  }
}
