package dev.kambaabi.validator;

import org.apache.maven.plugin.MojoFailureException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NotEndingWithValidator implements CommitTextValidator {

    private List<String> notEndsWithList;

    @Override
    public void registerArgs(String... args) {
        notEndsWithList = null != args && args.length > 0 ? Arrays.asList(args) : new ArrayList<>();
    }

    @Override
    public boolean validate(String value) throws Exception {
        if (notEndsWithList.size() < 1) {
            throw new MojoFailureException("Please assign at least one text value for checking whether given value is not ending with it!");
        }
        boolean out = null != value;
        for (String notEndsWith : notEndsWithList) {
            out = out && !value.endsWith(notEndsWith);
        }
        return out;
    }
}
