package dev.kambaabi;

import dev.kambaabi.configuration.CaptureGroupConfig;
import dev.kambaabi.configuration.CustomConfig;
import dev.kambaabi.configuration.RegexConfig;
import dev.kambaabi.configuration.ValidationConfig;
import dev.kambaabi.validator.CommitTextValidator;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static dev.kambaabi.Utils.isNonEmptyArray;
import static dev.kambaabi.Utils.stringNotEmpty;

@Mojo(name = "check", requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME, defaultPhase = LifecyclePhase.COMPILE)
public class MyMojo extends AbstractMojo {

    @Parameter
    private boolean skip;

    @Parameter
    private String testCommitMessage = "feAt(SCOPE)!: ornek deneme\nseni Allah bildiği gibi yapsın\n";

    @Parameter
    private boolean failOnError;

//    @Parameter
//    private final String regexOld = "^(build|chore|ci|docs|feat|fix|perf|refactor|revert|style|test){1}(\\([\\w\\-\\.]+\\))?(!)?: ([\\w ])+([\\s\\S]*)";

//    @Parameter
//    private final String regex = "^(\\w*)(?:\\(([\\w\\-.]+)\\))?(!)?: ([\\w ]+)(\\n[\\s\\S]*)?";

    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    private MavenProject project;

    @Parameter(property = "scope")
    String scope;


    @Parameter
    private CustomConfig customConfig;


    private ClassLoader customClassLoader;


    private void validateWithRegexConfig(RegexConfig regexconfig, String commitMessage) throws Exception {
        debug("Starting Regex Operation For: [%s] ", regexconfig.getValue());
        debug("Commit Message Is: [%s] ", commitMessage);
        final Pattern pattern = Pattern.compile(regexconfig.getValue());
        debug("Compiled Pattern Is: [%s] ", pattern.pattern());
        Matcher matcher = pattern.matcher(commitMessage);
        debug("Is Pattern Matching A Success : [%s] ", matcher.matches());
        if (!matcher.matches()) {
            debug("Pattern Matching Failed, Throwing MojoFailureException Exception!");
            error("Pattern Matching For The Commit Message Failed!");
            error("Commit Message: %s", commitMessage);
            error("Regex: %s", pattern.pattern());
            throw new MojoFailureException(String.format("Commit Message did not match!\nPattern: %s\nCommit Message: %s", pattern.pattern(), commitMessage));
        }
        debug("Config size: %d, capture groupsize: %d", regexconfig.getCaptureGroups().size(), matcher.groupCount());
        for (int i = 0; i < matcher.groupCount(); i++) {
            debug("Capture Group %d: %s", i, matcher.group(i));
        }

        if (!isNonEmptyArray(regexconfig.getCaptureGroups())) {
            debug("No CaptureGroup Validations Configured given for this Regex!");
        } else {
            debug("%d CaptureGroup Validations Defined, %d CaptureGroup Generated From Regex Pattern", regexconfig.getCaptureGroups().size(), matcher.groupCount());
            for (int i = 1; i <= Math.min(regexconfig.getCaptureGroups().size(), matcher.groupCount()); i++) {
                final int configIndex = i;
                Optional<CaptureGroupConfig> config = regexconfig.getCaptureGroups().stream()
                        .filter(captureGroupConfig -> captureGroupConfig.getIndex() == configIndex).findAny();
                String captureGroup = matcher.group(i);
                debug("Beginning Operations For Captrue Group %d:[%s]", i, captureGroup);
                if (!config.isPresent()) {
                    debug("Either capture group is empty or there's no validations defined for the capture group, skipping.");
                    continue;
                }
                List<ValidationConfig> validationConfigList = config.get().getValidations();
                if (!isNonEmptyArray(validationConfigList)) {
                    debug("There's no validations defined for the capture group, skipping.");
                    continue;
                }
                debug("There are %d validation(s) defined for the %d. CaptureGroup [%s], looping each one.",
                        validationConfigList.size(), i, captureGroup);
                for (ValidationConfig validationConfig : validationConfigList) {
                    if (
                            ValidationConfig.ValidationOpts.SKIP_IF_EMPTY.equals(validationConfig.getOpts()) ||
                                    (!stringNotEmpty(captureGroup) && validationConfig.getOpts().equals(ValidationConfig.ValidationOpts.SKIP_IF_EMPTY))
                    ) {
                        debug("Validation Skip Conditions Met for Validation %s, %d. Capture Group [%s], Validation Opts %s", validationConfig.getClassName(), i, captureGroup, validationConfig.getOpts());
                        continue;
                    }
                    loadValidationClassAndValidateForCaptureGroup(i, captureGroup, validationConfig);
                }
            }
        }

        if (!isNonEmptyArray(regexconfig.getSubjectValidations())) {
            debug("No Subject Validations given for this Regex!");
        } else {
//            Pattern lineSplitterPattern = Pattern.compile("^(.*\\n?)$");
//            Matcher lineMatcher = lineSplitterPattern.matcher(commitMessage);
//            if (!lineMatcher.matches()) {
//                debug("Pattern %s did not match!", lineSplitterPattern.pattern());
//                throw new MojoFailureException(String.format("Error occured calculating subject! Pattern %s did not match!", lineSplitterPattern.pattern()));
//            }
//            String subject = lineMatcher.group(1);
            String subject = commitMessage.split("\n")[0];
            if (!stringNotEmpty(subject)) {
                debug("Error occured calculating subject! Splitting by \n did not work!");
                throw new MojoFailureException(String.format("Error occured calculating subject! Splitting by \n did not work!"));
            }
            debug("There are %d validation(s) defined for the Subject [%s], looping each one.",
                    regexconfig.getSubjectValidations().size(), subject);
            for (ValidationConfig validationConfig : regexconfig.getSubjectValidations()) {
                if (
                        ValidationConfig.ValidationOpts.SKIP_IF_EMPTY.equals(validationConfig.getOpts()) ||
                                (!stringNotEmpty(subject) && validationConfig.getOpts().equals(ValidationConfig.ValidationOpts.SKIP_IF_EMPTY))
                ) {
                    debug("Validation Skip Conditions Met for Validation %s, Subject [%s], Validation Opts %s", validationConfig.getClassName(), subject, validationConfig.getOpts());
                    continue;
                }
                loadValidationClassAndValidateForCaptureGroup(-1, subject, validationConfig);
            }
        }

    }

    private void loadValidationClassAndValidateForCaptureGroup(int i, String captureGroup, ValidationConfig validationConfig) throws Exception {
        Class<CommitTextValidator> clazz = (Class<CommitTextValidator>) getClassLoader().loadClass(validationConfig.getClassName());
        debug("Loaded class: " + clazz.getName());
        String[] args = new String[0];
        if (isNonEmptyArray(validationConfig.getArgs())) {
            debug("Validation config has argument values for %s, getting.", clazz.getName());
            args = validationConfig.getArgs().toArray(new String[0]);
        }
        debug("There are %d args defined for %s: [%s] ", args.length, validationConfig.getClassName(), String.join(",", args));

        CommitTextValidator validator = clazz.newInstance();
        debug("Instance generated for %s", validator.getClass().getSimpleName());
        validator.registerArgs(args);
        debug("Args registered for the generated instance of %s: [%s]", validator.getClass().getSimpleName(), String.join(",", args));
        boolean result = validator.validate(captureGroup);
        debug("level: [%s] %s(%s) validation result for Capture Group %d [%s]: %s", validationConfig.getLevel(), validationConfig.getClassName(), String.join(",", args), i, captureGroup, result);
        if (!result && validationConfig.getLevel().equals(ValidationConfig.ValidationLevels.WARN)) {
            warn("[%s] %s(%s) validation result: %s", validationConfig.getLevel(), validator.getClass().getSimpleName(), String.join(",", args), result);
        } else if (!result && validationConfig.getLevel().equals(ValidationConfig.ValidationLevels.ERROR)) {
            String msg = String.format("%s(%s) validation result for Capture Group %d [%s]: %s", validator.getClass().getSimpleName(), String.join(",", args), i, captureGroup, result);
            error(msg);
            throw new MojoFailureException("failed commit message checks! " + msg);
        }
    }


    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (skip) {
            this.getLog().info("Skipping Commitlint.");
            return;
        }
        try {
            // Ensure that 'this.configuration' is not null before accessing its methods or fields.
            if (this.customConfig != null) {
                String commitMessage = null;
                if (null != this.testCommitMessage) {
                    commitMessage = this.testCommitMessage;
                    warn("Checking given commit message:\n%s", testCommitMessage);
                }
                List<RegexConfig> regexConfigList = customConfig.getRegexes();
                for (RegexConfig regex : customConfig.getRegexes()) {
                    validateWithRegexConfig(regex, testCommitMessage);
                }
            } else {
                getLog().error("Configuration is null. Check your plugin configuration!");
                throw new MojoFailureException("Configuration is null. Check your plugin configuration!s");
            }
            if (failOnError) {
                throw new MojoFailureException("Commit Lint failed, please check rules");
            }
        } catch (PatternSyntaxException ex) {
            debug("PatternSyntaxException thrown! regex: %s", ex.getPattern());
            throw new MojoFailureException(ex.getLocalizedMessage());
        } catch (Exception ex) {
            throw new MojoFailureException("Unable to lint commit message due to exception!\n" + ex.getLocalizedMessage());
        }

    }

    public boolean isSkip() {
        return skip;
    }

    public void setSkip(boolean skip) {
        this.skip = skip;
    }

    public boolean isFailOnError() {
        return failOnError;
    }

    public void setFailOnError(boolean failOnError) {
        this.failOnError = failOnError;
    }

    public CustomConfig getCustomConfig() {
        return customConfig;
    }

    public void setCustomConfig(CustomConfig customConfig) {
        this.customConfig = customConfig;
    }

    private void info(String msg, Object... args) {
        getLog().info(String.format(msg, args));
    }

    private void error(String msg, Object... args) {
        getLog().error(String.format(msg, args));
    }

    private void debug(String msg, Object... args) {
        getLog().debug(String.format(msg, args));
    }

    private void warn(String msg, Object... args) {
        getLog().warn(String.format(msg, args));
    }

    public MavenProject getProject() {
        return project;
    }

    public void setProject(MavenProject project) {
        this.project = project;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    private ClassLoader getClassLoader() {
        if (null != customClassLoader) {
            return customClassLoader;
        }
        ClassLoader classLoader = null;
        try {
            List<String> classpathElements = project.getRuntimeClasspathElements();
            if (null == classpathElements) {
                return Thread.currentThread().getContextClassLoader();
            }
            URL[] urls = new URL[classpathElements.size()];

            for (int i = 0; i < classpathElements.size(); ++i) {
                urls[i] = new File((String) classpathElements.get(i)).toURI().toURL();
            }
            classLoader = new URLClassLoader(urls, getClass().getClassLoader());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return customClassLoader = classLoader;
    }

}
