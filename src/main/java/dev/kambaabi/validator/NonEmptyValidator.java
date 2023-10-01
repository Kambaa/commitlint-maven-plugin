package dev.kambaabi.validator;

public class NonEmptyValidator implements CommitTextValidator {

    @Override
    public void registerArgs(String... args) {
    }

    @Override
    public boolean validate(String value) throws Exception {
        return null != value && !value.equalsIgnoreCase("");
    }
}
