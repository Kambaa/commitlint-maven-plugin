package dev.kambaabi.validator;

import org.apache.maven.plugin.logging.Log;

public interface CommitTextValidator {
    public boolean validate(String value, Log log) throws Exception;
}
