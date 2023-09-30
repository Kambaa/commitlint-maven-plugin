package dev.kambaabi;

import dev.kambaabi.configuration.CaptureGroupConfig;
import dev.kambaabi.configuration.CustomConfig;
import dev.kambaabi.configuration.RegexConfig;
import dev.kambaabi.configuration.ValidationConfig;
import dev.kambaabi.validator.CommitTextValidator;
import org.apache.maven.model.Dependency;
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

@Mojo(name = "check", requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME, defaultPhase = LifecyclePhase.COMPILE)
public class MyMojo extends AbstractMojo {

    @Parameter
    private boolean skip;

    @Parameter
    private String testCommitMessage = "feAt(SCOPE)!: ornek deneme\nseni Allah bildiği gibi yapsın\n";


    @Parameter
    private String cn;

    @Parameter
    private boolean failOnError;

//    @Parameter
//    private final String regexOld = "^(build|chore|ci|docs|feat|fix|perf|refactor|revert|style|test){1}(\\([\\w\\-\\.]+\\))?(!)?: ([\\w ])+([\\s\\S]*)";

//    @Parameter
//    private final String regex = "^(\\w*)(?:\\(([\\w\\-.]+)\\))?(!)?: ([\\w ]+)(\\n[\\s\\S]*)?";


//    @Parameter
//    private CaptureGroup[] captureGroups = new CaptureGroup[0];

    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    private MavenProject project;

    @Parameter(property = "scope")
    String scope;


    @Parameter
    private CustomConfig customConfig;


    /**
     * @parameter expression="${project.build.directory}/${project.build.finalName}.jar"
     * @readonly
     */
    private String projectJar;


    private void validateWithRegexConfig(RegexConfig regexconfig, String commitMessage) throws MojoFailureException {
        try {
            debug("Starting Regex Operation For: [%s] ", regexconfig.getValue());
            debug("Commit Message Is: [%s] ", commitMessage);
            final Pattern pattern = Pattern.compile(regexconfig.getValue());
            debug("Compiled Pattern Is: [%s] ", pattern.pattern());
            Matcher matcher = pattern.matcher(commitMessage);
            debug("Is Pattern Matching A Success : [%s] ", matcher.matches());
            if (!matcher.matches()) {
                debug("Pattern Matching Failed, Throwing MojoFailureException Exception!");
                throw new MojoFailureException(String.format("Commit Message did not matched the following pattern: [%s]", regexconfig.getValue()));
            }
//            debug("Configsize: %d, captureGroupsize: %d", regexconfig.getCaptureGroups().size(), matcher.groupCount());

            if (!isNonEmptyArray(regexconfig.getCaptureGroups())) {
                debug("No CaptureGroup Validations Configured given for this Regex!");
            } else {
                debug("%d CaptureGroup Validations Defined, %d CaptureGroup Generated From Regex Pattern", regexconfig.getCaptureGroups().size(), matcher.groupCount());
                for (int i = 1; i <= Math.min(regexconfig.getCaptureGroups().size(), matcher.groupCount()); i++) {
                    final int configIndex = i;
                    Optional<CaptureGroupConfig> config = regexconfig.getCaptureGroups().stream()
                            .filter(captureGroupConfig -> captureGroupConfig.getIndex() == configIndex).findAny();
                    String captureGroup = matcher.group(i);
                    if (null == captureGroup) {
                        debug("%d. Capture Group is null, skipping.", i);
                        continue;
                    }
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

                        debug("ValidationConfig ITERATION START!");
                        debug("Classname to check: " + validationConfig.getClassName());
                        debug("Arg size: " + validationConfig.getArgs().size());
                        debug("First Arg is" + validationConfig.getArgs().get(0));
                        debug("Level: " + validationConfig.getLevel());


                        Class<CommitTextValidator> clazz = (Class<CommitTextValidator>) getClassLoader().loadClass(validationConfig.getClassName());
                        debug("Loaded class: " + clazz.getName());

                        debug("There are %d args defined for %s ", isNonEmptyArray(validationConfig.getArgs()) ?
                                validationConfig.getArgs().size() : 0, validationConfig.getClassName());
                        String[] args = isNonEmptyArray(validationConfig.getArgs()) ? (String[]) validationConfig.getArgs().toArray() : new String[0];
                        CommitTextValidator validator = clazz.newInstance().createInstance();
                        boolean result = validator.validate(captureGroup);
                        debug("[%s] %s validation result: %s", validationConfig.getLevel(), validationConfig.getClassName(), result);
                        warn("[%s] %s validation result: %s", validationConfig.getLevel(), validationConfig.getClassName(), result);
                        if (!result) {
                            throw new MojoFailureException("failed commit message checks!");
                        }
                    }

//                    info("Matcher Group %d is: [%s]", i, matcher.group(i));
//                    final String extractedContent = extractContent(matcher.group(i));
//                    this.getLog().debug(extractedContent);
//                    final RuleChecker checker = new RuleChecker(captureGroups[i - 1], this.getLog());
//                    result += checker.check(extractedContent);
                }
            }


        } catch (
                PatternSyntaxException ex) {
            getLog().debug("PatternSyntaxException thrown! Generating Error Message And Throwing MojoFailureException!");
            String msg = String.format("Given Regex String Is Not Valid: [%s] ", regexconfig.getValue());
            getLog().error(msg);
            throw new MojoFailureException(ex.getLocalizedMessage());
        } catch (
                Exception e) {
            new MojoFailureException(e);
        }


    }

    private ClassLoader getClassLoader() {
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
        return classLoader;
    }


    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (skip) {
            this.getLog().info("Skipping Commitlint.");
            return;
        }
        try {

//            Class<?> clazz = getClassLoader().loadClass(getCn());
//            getLog().info("Loaded:" + clazz.toString());

            // Ensure that 'this.configuration' is not null before accessing its methods or fields.
            if (this.customConfig != null) {

                String commitMessage = null;
                if (null != this.testCommitMessage) {
                    commitMessage = this.testCommitMessage;
//                    getLog().info(String.format("Use test commit message [%s]", this.testCommitMessage));
                }

                List<RegexConfig> regexConfigList = customConfig.getRegexes();
//                getLog().error("REGEXLIST SIZE IS: " + customConfig.getRegexes().size());
                for (RegexConfig regex : customConfig.getRegexes()) {
                    validateWithRegexConfig(regex, testCommitMessage);
                }
            } else {
                // Handle the case where 'this.configuration' is null.
                getLog().error("Configuration is null. Check your plugin configuration.");
            }

            int result = 0;

//            final Pattern pattern = Pattern.compile(this.matchPattern);
//            Matcher matcher = pattern.matcher(commitMessage);
//            if (!matcher.matches()) {
//                throw new MojoFailureException(String.format("Message did not matched the following pattern: %s", matchPattern));
//            }

//            RegexValidator regexValidator = new RegexValidator(regex);
//            if (!regexValidator.validate(commitMessage, getLog())) {
//                String msg1 = "CommitLint validation error:";
//                String msg2 = String.format("Validation: %s(`%s`)", regexValidator.getClass().getSimpleName(), regex);
//
//                getLog().error(msg1);
//                getLog().error(msg2);
//                getLog().error(String.format("Failed commit message:\n%s", commitMessage));
//                getLog().error("Please check your commit message.");
//                throw new MojoFailureException(msg1 + " " + msg2);
//            }
//
//            final Pattern pattern = Pattern.compile(this.regex);
//            Matcher matcher = pattern.matcher(commitMessage);
//            if (matcher.matches()) {
//                for (int i = 0; i <= matcher.groupCount(); i++) {
//                    getLog().info(String.format("Matcher Group %d is: [%s]", i, matcher.group(i)));
//////                final String extractedContent = extractContent(matcher.group(i));
//////                this.getLog().debug(extractedContent);
//////                final RuleChecker checker = new RuleChecker(captureGroups[i - 1], this.getLog());
//////                result += checker.check(extractedContent);
//                }
//            }
            if (0 == result) {
                return;
            }
            if (failOnError) {
                throw new MojoFailureException("Commit Lint failed, please check rules");
            }
        } catch (
                Exception ex) {
            ex.printStackTrace();
            throw new MojoFailureException("Unable to lint commit message due to exception");
        }

        List<Dependency> dependencies = project.getDependencies();
        long numDependencies = dependencies.stream()
                .filter(d -> (scope == null || scope.isEmpty()) || scope.equals(d.getScope()))
                .count();

        getLog().

                info("Number of dependencies: " + numDependencies);

    }


    /**
     * Extract content from special characters.
     *
     * @param capturedString entire commit message
     * @return the pattern matched string
     * @throws MojoFailureException When Mojo failed
     */
    private String extractContent(final String capturedString) throws MojoFailureException {
        final Pattern pattern = Pattern.compile("[\\w\\d\\s-_]+");
        final Matcher matcher = pattern.matcher(capturedString);

        if (!matcher.find()) {
            throw new MojoFailureException(String.format("No content found [%s]", capturedString));
        }
        return matcher.group().trim();
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

    public String getCn() {
        return cn;
    }

    public void setCn(String cn) {
        this.cn = cn;
    }


}
