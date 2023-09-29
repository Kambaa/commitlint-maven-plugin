package dev.kambaabi.configuration;

import org.apache.maven.plugins.annotations.Parameter;

import java.util.List;

public class Regex {
    @Parameter
    private String value;

    @Parameter(property = "captureGroup")
    private List<CaptureGroup> captureGroups;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public List<CaptureGroup> getCaptureGroups() {
        return captureGroups;
    }

    public void setCaptureGroups(List<CaptureGroup> captureGroups) {
        this.captureGroups = captureGroups;
    }
}