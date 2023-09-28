package dev.kambaabi.validator;

import org.apache.maven.plugin.MojoFailureException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConventionalCommitsRegexValidator implements CommitTextValidator {


    /**
     * Found this at https://gist.github.com/marcojahn/482410b728c31b221b70ea6d2c433f0c.
     */
    private final Pattern pattern = Pattern.compile("^(build|chore|ci|docs|feat|fix|perf|refactor|revert|style|test){1}(\\([\\w\\-\\.]+\\))?(!)?: ([\\w ])+([\\s\\S]*)");


    @Override
    public boolean validate(String value) throws MojoFailureException {
        Matcher matcher = pattern.matcher(value);
        return matcher.matches();
    }

}
