package com.eugenenosenko.conventional.changelog.plugin.file;

import com.eugenenosenko.conventional.changelog.plugin.context.AbstractFileHandler;

import java.nio.file.Path;

public final class ChangeLogHandler extends AbstractFileHandler {
  public ChangeLogHandler(Path path) {
    super(path);
  }
}
