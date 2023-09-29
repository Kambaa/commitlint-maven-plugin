package dev.kambaabi.validator;

import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;

import java.util.regex.Pattern;

public class RegexValidator implements CommitTextValidator {

    private final Pattern pattern;

    public RegexValidator(String... args) {
        pattern = Pattern.compile(args[0]);
    }

    @Override
    public boolean validate(String value, Log log) throws MojoFailureException {
        return pattern.matcher(value).matches();

    }
}
