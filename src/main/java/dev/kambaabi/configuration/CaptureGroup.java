package dev.kambaabi.configuration;

import org.apache.maven.plugins.annotations.Parameter;

import java.util.List;

public class CaptureGroup {
    @Parameter
    private String name;

    @Parameter(property = "validation")
    private List<Validation> validations;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Validation> getValidations() {
        return validations;
    }

    public void setValidations(List<Validation> validations) {
        this.validations = validations;
    }

}
