package dev.kambaabi.configuration;

import org.apache.maven.plugins.annotations.Parameter;

import java.util.List;

public class CaptureGroupConfig {

    @Parameter
    private int index;

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
