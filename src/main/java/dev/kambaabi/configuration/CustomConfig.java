package dev.kambaabi.configuration;

import org.apache.maven.plugins.annotations.Parameter;

import java.util.List;

public class CustomConfig {
    @Parameter(property = "regex")
    private List<RegexConfig> regexes;

    public List<RegexConfig> getRegexes() {
        return regexes;
    }

    public void setRegexes(List<RegexConfig> regexes) {
        this.regexes = regexes;
    }
}
