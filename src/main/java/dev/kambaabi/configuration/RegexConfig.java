package dev.kambaabi.configuration;

import org.apache.maven.plugins.annotations.Parameter;

import java.util.List;

public class RegexConfig {
    @Parameter
    private String value;

    @Parameter(property = "captureGroup")
    private List<CaptureGroupConfig> captureGroupConfigs;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public List<CaptureGroupConfig> getCaptureGroups() {
        return captureGroupConfigs;
    }

    public void setCaptureGroups(List<CaptureGroupConfig> captureGroupConfigs) {
        this.captureGroupConfigs = captureGroupConfigs;
    }
}