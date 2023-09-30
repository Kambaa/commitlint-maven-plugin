package dev.kambaabi.validator;

import org.apache.maven.plugin.MojoFailureException;

import java.util.regex.Pattern;

public class RegexValidator implements CommitTextValidator {

    private Pattern pattern;

    public RegexValidator() {

    }

    @Override
    public void registerArgs(String... args) {
        pattern = Pattern.compile(args[0]);
    }

    @Override
    public boolean validate(String value) throws MojoFailureException {
        return pattern.matcher(value).matches();

    }
}
