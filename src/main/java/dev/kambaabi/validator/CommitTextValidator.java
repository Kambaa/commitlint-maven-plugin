package dev.kambaabi.validator;

public interface CommitTextValidator {
    void registerArgs(String... args);

    boolean validate(String value) throws Exception;
}
