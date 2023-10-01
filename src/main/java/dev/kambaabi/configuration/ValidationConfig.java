package dev.kambaabi.configuration;

import org.apache.maven.plugins.annotations.Parameter;

import java.util.Arrays;
import java.util.List;


/**
 * Custom Validation Definitions.
 */
public class ValidationConfig {
    /**
     * Validation levels enum.
     * ERROR: returns error on validation fail.
     * WARN: returns a warning on validation fail.
     */
    public enum ValidationLevels {
        /**
         * Returns error on validation fail.
         */
        ERROR,
        /**
         * Returns a warning on validation fail.
         */
        WARN;

        /**
         * Get enum from given string.
         */
        public static ValidationLevels getByString(String name) {
            return Arrays.stream(ValidationLevels.values())
                    .filter(validationLevels -> validationLevels.toString().equalsIgnoreCase(name))
                    .findAny()
                    .orElseThrow(() -> new IllegalArgumentException("Unknown ValidationLevels: " + name));

        }
    }

    /**
     * Validation extra options enum.
     * SKIP_IF_EMPTY: If capture group to check is an empty string, skip this validation.
     * SKIP: skips the validation.
     */
    public enum ValidationOpts {
        /**
         * Default normal operation.
         */
        NO_SKIP,
        /**
         * If capture group to check is an empty string, skip this validation.
         */
        SKIP_IF_EMPTY,
        /**
         * Skips the validation.
         */
        SKIP;

        /**
         * Get enum from given string.
         */
        public static ValidationOpts getByString(String name) {
            return Arrays.stream(ValidationOpts.values())
                    .filter(validationLevels -> validationLevels.toString().equalsIgnoreCase(name))
                    .findAny()
                    .orElseThrow(() -> new IllegalArgumentException("Unknown ValidationOpts: " + name));
        }
    }

    /**
     * Class name that does the validation operation.
     * Note that validation classes should extend dev.kambaabi.validator.CommitTextValidator interface to be able to work.
     */
    @Parameter
    private String className;

    /**
     * Custom List of string arguments for the Custom Validation class.
     */
    @Parameter(property = "arg")
    private List<String> args;

    /**
     * Validation levels.
     *
     * @see ValidationLevels
     */
    @Parameter(defaultValue = "ERROR", property = "level")
    private ValidationLevels level;

    /**
     * Validation extra options.
     *
     * @see ValidationOpts
     */
    @Parameter(defaultValue = "NO_SKIP", property = "opts")
    private ValidationOpts opts;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public List<String> getArgs() {
        return args;
    }

    public void setArgs(List<String> args) {
        this.args = args;
    }

    public ValidationLevels getLevel() {
        return level;
    }

    public void setLevel(ValidationLevels level) {
        this.level = level;
    }

    public ValidationOpts getOpts() {
        return opts;
    }

    public void setOpts(ValidationOpts opts) {
        this.opts = opts;
    }
}
