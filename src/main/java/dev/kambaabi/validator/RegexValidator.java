package dev.kambaabi.validator;

import org.apache.maven.plugin.MojoFailureException;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class RegexValidator implements CommitTextValidator {

    private List<Pattern> patterns = new ArrayList<>();

    public RegexValidator(String... args) {
        for (String regex : args) {
            patterns.add(Pattern.compile(regex));
        }
    }

    @Override
    public boolean validate(String value) throws MojoFailureException {
        boolean out = true;
        if (patterns.size() > 0) {
            for (Pattern pattern : patterns) {
                out = out && pattern.matcher(value).matches();
            }
        }
        return out;
    }
}
