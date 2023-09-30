package dev.kambaabi.validator;

import org.apache.maven.plugin.MojoFailureException;

import java.util.regex.Pattern;

public class RegexValidator implements CommitTextValidator {

    private final Pattern pattern;

    public RegexValidator(String... args) {
        pattern = Pattern.compile(args[0]);
    }

    @Override
    public RegexValidator createInstance(String... args) {
        return new RegexValidator(args);
    }

    @Override
    public boolean validate(String value) throws MojoFailureException {
        return pattern.matcher(value).matches();

    }
}
