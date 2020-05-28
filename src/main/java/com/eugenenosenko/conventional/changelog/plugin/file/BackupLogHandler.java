package com.eugenenosenko.conventional.changelog.plugin.file;

import com.eugenenosenko.conventional.changelog.plugin.context.AbstractFileHandler;

import java.nio.file.Path;

public final class BackupLogHandler extends AbstractFileHandler {
  public BackupLogHandler(Path path) {
    super(path);
  }
}
