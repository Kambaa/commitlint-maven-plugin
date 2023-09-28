package dev.kambaabi;

import dev.kambaabi.validator.ConventionalCommitsRegexValidator;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Mojo(name = "check", defaultPhase = LifecyclePhase.COMPILE)
public class MyMojo extends AbstractMojo {

    @Parameter
    private boolean skip;

    @Parameter
    private String testCommitMessage = "feat(SCOPE)!: örnek deneme\nsenin Allah bildiği gibi yapsın";

    @Parameter
    private boolean failOnError;

    @Parameter
    private String matchPattern = "^(build|chore|ci|docs|feat|fix|perf|refactor|revert|style|test){1}(\\([\\w\\-\\.]+\\))?(!)?: ([\\w ])+([\\s\\S]*)";

    @Parameter
    private CaptureGroup[] captureGroups = new CaptureGroup[0];

    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    MavenProject project;

    @Parameter(property = "scope")
    String scope;


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

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (skip) {
            this.getLog().info("Skipping Commitlint.");
            return;
        }
        try {
            String commitMessage = null;
            if (null != this.testCommitMessage) {
                commitMessage = this.testCommitMessage;
                getLog().info(String.format("Use test commit message [%s]", this.testCommitMessage));
            }
            int result = 0;

//            final Pattern pattern = Pattern.compile(this.matchPattern);
//            Matcher matcher = pattern.matcher(commitMessage);
//            if (!matcher.matches()) {
//                throw new MojoFailureException(String.format("Message did not matched the following pattern: %s", matchPattern));
//            }

            ConventionalCommitsRegexValidator ccrv = new ConventionalCommitsRegexValidator();
            if (!ccrv.validate(commitMessage)) {
                String msg1 = String.format("CommitLint validation error:");
                String msg2 = String.format("Validation: %s", ccrv.getClass().getSimpleName());

                getLog().error(msg1);
                getLog().error(msg2);
                getLog().error(String.format("Failed commit message:\n%s", commitMessage));
                getLog().error("Please check your commit message.");
                throw new MojoFailureException(msg1 + " " + msg2);
            }

//            for (int i = 0; i <= matcher.groupCount(); i++) {
//                getLog().info(String.format("Matcher Group %d is: [%s]", i, matcher.group(i)));
//
////                final String extractedContent = extractContent(matcher.group(i));
////                this.getLog().debug(extractedContent);
////                final RuleChecker checker = new RuleChecker(captureGroups[i - 1], this.getLog());
////                result += checker.check(extractedContent);
//            }
            if (0 == result) {
                return;
            }
            if (failOnError) {
                throw new MojoFailureException("Commit Lint failed, please check rules");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new MojoFailureException("Unable to lint commit message due to exception");
        }

        List<Dependency> dependencies = project.getDependencies();
        long numDependencies = dependencies.stream()
                .filter(d -> (scope == null || scope.isEmpty()) || scope.equals(d.getScope()))
                .count();
        getLog().info("Number of dependencies: " + numDependencies);
    }
}
