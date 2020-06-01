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

  /**
   * Filename where the changelog should be copied to
   *
   * <p>defaultValue: CHANGELOG.md
   */
  @Parameter(property = "filename", defaultValue = "CHANGELOG.md")
  protected String filename;

  /**
   * Backup filename
   *
   * <p>defaultValue: CHANGELOG.md.backup
   *
   * <p>If changelog file already exists then the contents will be copied to this backup file
   */
  @Parameter(property = "backupFilename", defaultValue = "CHANGELOG.md.backup")
  protected String backupFilename;

  /**
   * Project root directory. .git directory should be located in the same folder as well as any
   * previous CHANGELOG.md file
   */
  @Parameter(defaultValue = "${project.basedir}", required = true)
  protected File projectBaseDir;

  /**
   * If set to true, plugin will automatically amend last commit and add CHANGELOG.md file and
   * reapply any tags if any where present
   *
   * <p>defaultValue: true
   */
  @Parameter(property = "amendLastCommit", defaultValue = "false")
  protected boolean amendLastCommit;

  /**
   * Number of past releases that changelog should be generated for
   *
   * <p>If set to -1, changelog will be generate for full git history
   *
   * <p>If set to 0, changelog will be generated for last release
   *
   * <p>If set to 2, changelog will be generated for last 3 releases
   *
   * <p>If the value presented is larger than tag count, it will be treated same as -1 argument
   *
   * <p>defaultValue: -1
   */
  @Parameter(property = "releaseCount", defaultValue = "-1")
  protected int releaseCount;

  protected DefaultGitService createGitService() throws IOException {
    getLog().info("Starting git service...");
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
