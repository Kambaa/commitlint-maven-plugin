package dev.kambaabi.validator;

import org.apache.maven.plugin.MojoFailureException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class CaseValidator implements CommitTextValidator {

    public enum Case {
        UPPERCASE, LOWERCASE,
        UPPERCAMELCASE, LOWERCAMELCASE,
        KEBABCASE,
        SNAKECASE,
        SENTENCECASE,
        NONE;

        public static Case getCaseByString(String str) {
            for (Case value : Case.values()) {
                if (value.name().equalsIgnoreCase(str)) {
                    return value;
                }
            }
            throw new IllegalArgumentException("No Case found with given: " + str);
        }
    }

    private List<Case> selectedCases = Collections.singletonList(Case.NONE);

    public CaseValidator() {
    }

    @Override
    public void registerArgs(String... args) {
        if (null != args && args.length > 0) {
            selectedCases = new ArrayList<>();
            for (int i = 0; i < args.length; i++) {
                selectedCases.add(Case.getCaseByString(args[i]));
            }
        }
    }

    @Override
    public boolean validate(String value) throws MojoFailureException {
        boolean result = true;
        for (int i = 0; i < selectedCases.size(); i++) {
            Case pattern = selectedCases.get(i);
            switch (pattern) {
                case LOWERCASE:
                    result = result & isLowerCase(value);
                    break;
                case UPPERCASE:
                    result = result & isUpperCase(value);
                    break;
                case UPPERCAMELCASE:
                    result = result & isUpperCamelCase(value);
                    break;
                case LOWERCAMELCASE:
                    result = result & isLowerCamelCase(value);
                    break;
                case KEBABCASE:
                    result = result & isKebabCase(value);
                    break;
                case SNAKECASE:
                    result = result & isSnakeCase(value);
                    break;
                case SENTENCECASE:
                    result = result & isSentenceCase(value);
                    break;
                case NONE:
                default:
            }
        }
        return result;
    }


    private static boolean isCamelCase(final String text) {
        return !text.contains("_")
                && !text.contains("-")
                && !text.contains(" ");
    }

    public static boolean isUpperCamelCase(final String text) {
        return isCamelCase(text) && Character.isUpperCase(text.charAt(0));
    }

    public static boolean isLowerCamelCase(final String text) {
        return isCamelCase(text) && Character.isLowerCase(text.charAt(0));
    }

    public static boolean isUpperCase(final String text) {
        return text.toUpperCase(Locale.ENGLISH).equals(text);
    }

    public static boolean isLowerCase(final String text) {
        return text.toLowerCase(Locale.ENGLISH).equals(text);
    }

    public static boolean isKebabCase(final String text) {
        return text.toLowerCase(Locale.ENGLISH).replaceAll("_", "-").replaceAll(" ", "-").equals(text);
    }

    public static boolean isSentenceCase(final String text) {
        return Character.isUpperCase(text.charAt(0)) && isLowerCase(text.substring(1));
    }

    public static boolean isSnakeCase(final String text) {
        return text.toLowerCase(Locale.ENGLISH).replace(" ", "_").replace("-", "_").equals(text);
    }
}
