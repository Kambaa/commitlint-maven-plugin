package dev.kambaabi.validator;

import org.apache.maven.plugin.MojoFailureException;

public interface CommitTextValidator {
    public boolean validate(String value) throws MojoFailureException;
}
