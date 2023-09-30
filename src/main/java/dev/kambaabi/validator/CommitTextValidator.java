package dev.kambaabi.validator;

public interface CommitTextValidator {
    CommitTextValidator createInstance(String... args);

    boolean validate(String value) throws Exception;
}
