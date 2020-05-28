package com.eugenenosenko.conventional.changelog.plugin.exception;

import org.apache.maven.plugin.MojoExecutionException;

public class TagSearchException extends MojoExecutionException {
  public TagSearchException(String message) {
    super(message);
  }
}
