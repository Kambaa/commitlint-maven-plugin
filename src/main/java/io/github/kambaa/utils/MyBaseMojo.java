package io.github.kambaa.utils;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Base mojo abstract class.
 */
public class MyBaseMojo extends AbstractMojo {
  /**
   * Skips execution if true.
   */
  @Parameter(defaultValue = "false")
  protected boolean skip = false;

  /**
   * If sets to false, validations errors will be be a build failure, instead messages will be logged as an error.
   */
  @Parameter(defaultValue = "true")
  protected boolean failOnError = true;

  /**
   * Check maven command is given with -X debug option.
   */
  @Parameter(property = "maven.logging.debug")
  protected boolean mavenDebug;

  /**
   * Common controls.
   */
  @Override
  public void execute() throws MojoFailureException {

  }

  /**
   * log info.
   *
   * @param msg  msg
   * @param args args
   */
  protected void info(String msg, Object... args) {
    getLog().info(String.format(msg, args));
  }

  /**
   * log error.
   *
   * @param msg  msg
   * @param args args
   */
  protected void error(String msg, Object... args) {
    getLog().error(String.format(msg, args));
  }

  /**
   * log debug.
   *
   * @param msg  msg
   * @param args args
   */
  protected void debug(String msg, Object... args) {
    // getLog().debug(String.format(msg, args));
    // todo: no logs displayed when debugging tests. remove below and uncomment above later.
    System.err.println(String.format(msg, args));
  }

  /**
   * log warn.
   *
   * @param msg  msg
   * @param args args
   */
  protected void warn(String msg, Object... args) {
    getLog().warn(String.format(msg, args));
  }

  /**
   * exit failing if failOnError is true, log error otherwise.
   *
   * @param msg  msg
   * @param args args
   * @throws MojoFailureException given error message if failOnError setting is true.
   */
  protected void exit(String msg, Object... args) throws MojoFailureException {
    if (failOnError) {
      throw new MojoFailureException(String.format(msg, args));
    }
    error(msg, args);
  }

  /**
   * getter skip.
   *
   * @return boolean
   */
  public boolean isSkip() {
    return skip;
  }

  /**
   * setter skip.
   *
   * @param skip skip
   */
  public void setSkip(boolean skip) {
    this.skip = skip;
  }

  /**
   * getter failOnError.
   *
   * @return boolean
   */
  public boolean isFailOnError() {
    return failOnError;
  }

  /**
   * setter failOnError.
   *
   * @param failOnError failOnError
   */
  public void setFailOnError(boolean failOnError) {
    this.failOnError = failOnError;
  }

  public boolean isMavenDebug() {
    return mavenDebug;
  }

  public void setMavenDebug(boolean mavenDebug) {
    this.mavenDebug = mavenDebug;
  }

  /**
   * Constructor.
   */
  public MyBaseMojo() {
  }
}
