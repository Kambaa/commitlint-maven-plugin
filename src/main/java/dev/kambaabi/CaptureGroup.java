package dev.kambaabi;

import dev.kambaabi.validator.StringCaseValidator;

public class CaptureGroup {
    private StringCaseValidator.Case caseFormat = StringCaseValidator.Case.NONE;
    private int max = Integer.MAX_VALUE;
    private int min = 0;

    public CaptureGroup(StringCaseValidator.Case caseFormat, int max, int min) {
        this.caseFormat = caseFormat;
        this.max = max;
        this.min = min;
    }

    public StringCaseValidator.Case getCaseFormat() {
        return caseFormat;
    }

    public int getMax() {
        return max;
    }

    public int getMin() {
        return min;
    }
}
