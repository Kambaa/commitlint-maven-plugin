package dev.kambaabi.validator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EnumValidator implements CommitTextValidator {

    private final List<String> choices = new ArrayList<>();

    @Override
    public void registerArgs(String... args) {
        choices.addAll(Arrays.asList(args));
        if (choices.size() == 0) {
            System.err.println("[EnumValidator] no choice registered");
        }
    }

    @Override
    public boolean validate(String value) throws Exception {
        return choices.stream().anyMatch(s -> s.equals(value));
    }
}
