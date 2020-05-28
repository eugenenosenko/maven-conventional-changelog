package com.eugenenosenko.conventional.changelog.plugin;

import com.eugenenosenko.conventional.changelog.plugin.file.BackupLogHandler;
import com.eugenenosenko.conventional.changelog.plugin.file.ChangeLogHandler;
import com.eugenenosenko.conventional.changelog.plugin.file.LogFileHandler;
import com.eugenenosenko.conventional.changelog.plugin.git.DefaultGitService;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

abstract class AbstractChangeLogMojo extends AbstractMojo {

  @Parameter(property = "filename", defaultValue = "CHANGELOG.md")
  protected String filename;

  @Parameter(property = "backupFilename", defaultValue = "CHANGELOG.md.backup")
  protected String backupFilename;

  @Parameter(defaultValue = "${project.basedir}", required = true)
  protected File projectBaseDir;

  protected DefaultGitService createGitService() throws IOException {
    Repository repository =
        new RepositoryBuilder().readEnvironment().findGitDir(projectBaseDir).build();
    getLog()
        .info(
            "Resolved .git repository in "
                + projectBaseDir
                + " for branch "
                + repository.getFullBranch());
    return new DefaultGitService(repository);
  }

  protected LogFileHandler createChangeLogFileHandler() {
    getLog().info("Looking for " + filename + " file in project dir.");
    Path path = Paths.get(projectBaseDir.getAbsolutePath(), filename);
    return new ChangeLogHandler(path);
  }

  protected LogFileHandler createBackupLogFileHandler() {
    Path filePath = Paths.get(projectBaseDir.getAbsolutePath(), backupFilename);
    try {
      Files.deleteIfExists(filePath);
    } catch (IOException e) {
      getLog().warn("Failed to delete backup file [" + backupFilename + "], will try to continue");
    }

    return new BackupLogHandler(filePath);
  }
}
