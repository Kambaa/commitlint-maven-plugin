package io.github.kambaa;

import static io.github.kambaa.utils.Utils.isEmpty;

import io.github.kambaa.utils.MyBaseMojo;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Checks commit message.
 */
@Mojo(name = "getDelimiter")
public class GetDelimiter extends MyBaseMojo {
  /**
   * Constructor.
   */
  public GetDelimiter() {
  }

  /**
   * Default splitting text(without quotes).
   * "\n------------------------ &gt;8 ------------------------\n"
   */
  public static final String DEFAULT_SEPARATOR = "\n------------------------ >8 ------------------------\n";


  @Override
  public void execute() throws MojoFailureException {
    getLog().info(DEFAULT_SEPARATOR);
  }





}
