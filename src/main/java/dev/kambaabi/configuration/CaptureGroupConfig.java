package dev.kambaabi.configuration;

import org.apache.maven.plugins.annotations.Parameter;

import java.util.List;

/**
 * Capture group custom validation definitions.
 */
public class CaptureGroupConfig {

    /**
     * Capture Group Number of the regex. Start with 1 for individual capture group validation definitions.
     */
    @Parameter
    private int index;

    /**
     * Custom validation definition list for the capture group.
     */
    @Parameter(property = "validation")
    private List<ValidationConfig> validations;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public List<ValidationConfig> getValidations() {
        return validations;
    }

    public void setValidations(List<ValidationConfig> validations) {
        this.validations = validations;
    }
}
