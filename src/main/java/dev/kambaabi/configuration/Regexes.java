package dev.kambaabi.configuration;

import org.apache.maven.plugins.annotations.Parameter;

import java.util.List;

public class Regexes {
    @Parameter
    private List<Regex> regex;

    public List<Regex> getRegex() {
        return regex;
    }

    public void setRegex(List<Regex> regex) {
        this.regex = regex;
    }
}