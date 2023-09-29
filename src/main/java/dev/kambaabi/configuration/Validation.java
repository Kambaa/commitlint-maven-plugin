package dev.kambaabi.configuration;

import org.apache.maven.plugins.annotations.Parameter;

import java.util.List;

public class Validation {
    @Parameter
    private String className;

    @Parameter(property = "arg")
    private List<String> args;

    @Parameter
    private String level;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public List<String> getArgs() {
        return args;
    }

    public void setArgs(List<String> args) {
        this.args = args;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

}
