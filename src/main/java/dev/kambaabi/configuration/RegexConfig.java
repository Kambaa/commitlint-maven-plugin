package dev.kambaabi.configuration;

import org.apache.maven.plugins.annotations.Parameter;

import java.util.List;

/**
 * Regular expression configuration that includes custom validation configurations for the capture groups for the given regular expression.
 */
public class RegexConfig {

    /**
     * Regex value.
     */
    @Parameter
    private String value;

    /**
     * Custom defined capture group validation configuration list.
     */
    @Parameter(property = "captureGroup")
    private List<CaptureGroupConfig> captureGroups;

    @Parameter(property = "validation")
    private List<ValidationConfig> subjectValidations;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public List<CaptureGroupConfig> getCaptureGroups() {
        return captureGroups;
    }

    public void setCaptureGroups(List<CaptureGroupConfig> captureGroupConfigs) {
        this.captureGroups = captureGroupConfigs;
    }

    public List<ValidationConfig> getSubjectValidations() {
        return subjectValidations;
    }

    public void setSubjectValidations(List<ValidationConfig> subjectValidations) {
        this.subjectValidations = subjectValidations;
    }
}