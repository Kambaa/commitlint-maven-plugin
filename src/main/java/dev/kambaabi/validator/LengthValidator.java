package dev.kambaabi.validator;

import org.apache.maven.plugin.MojoFailureException;

public class LengthValidator implements CommitTextValidator {

    private int min, max;

    public LengthValidator() {
    }

    public static boolean fitMax(final String text, final int max) {
        return text.length() <= max;
    }

    public static boolean fitMin(final String text, final int min) {
        return text.length() >= min;
    }

    @Override
    public void registerArgs(String... args) {
        this.min = Integer.parseInt(args[0]);
        this.max = Integer.parseInt(args[1]);
    }

    @Override
    public boolean validate(String value) throws MojoFailureException {
        if (min == 0 && max == 0) {
            throw new MojoFailureException("Please assign min and max values for LengthValidator!");
        }
        return fitMin(value, min) && fitMax(value, max);
    }
}
