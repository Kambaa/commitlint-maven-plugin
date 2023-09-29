package dev.kambaabi.configuration;

import org.apache.maven.plugins.annotations.Parameter;

import java.util.List;

public class CustomConfiguration {
    @Parameter(property = "regex")
    private List<Regex> regexes;

    public List<Regex> getRegexes() {
        return regexes;
    }

    public void setRegexes(List<Regex> regexes) {
        this.regexes = regexes;
    }
}
