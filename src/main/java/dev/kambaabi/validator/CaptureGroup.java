package dev.kambaabi.validator;

public class CaptureGroup {
    private CaseValidator.Case caseFormat = CaseValidator.Case.NONE;
    private int max = Integer.MAX_VALUE;
    private int min = 0;

    public CaptureGroup(CaseValidator.Case caseFormat, int max, int min) {
        this.caseFormat = caseFormat;
        this.max = max;
        this.min = min;
    }

    public CaseValidator.Case getCaseFormat() {
        return caseFormat;
    }

    public int getMax() {
        return max;
    }

    public int getMin() {
        return min;
    }
}
