package com.eugenenosenko.conventional.changelog.plugin;

import com.eugenenosenko.conventional.changelog.core.entry.ReleaseEntry;
import com.eugenenosenko.conventional.changelog.plugin.context.DefaultReleaseEntryResolver;
import com.eugenenosenko.conventional.changelog.plugin.context.VersionTag;
import com.eugenenosenko.conventional.changelog.plugin.exception.TagSearchException;
import com.eugenenosenko.conventional.changelog.plugin.file.LogFileHandler;
import com.eugenenosenko.conventional.changelog.plugin.git.DefaultGitService;
import com.eugenenosenko.conventional.changelog.plugin.git.GitVersionManager;
import com.eugenenosenko.conventional.changelog.plugin.parser.ChangelogManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.revwalk.RevCommit;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Mojo(name = "conventional", aggregator = true, defaultPhase = LifecyclePhase.VALIDATE)
public final class ChangeLogMojo extends AbstractChangeLogMojo {

  @Override
  public void execute() throws MojoExecutionException {
    LogFileHandler changeLog = createChangeLogFileHandler();
    LogFileHandler backupFile = createBackupLogFileHandler();

    try (DefaultGitService gitService = createGitService();
        ChangelogManager changelogManager =
            new ChangelogManager(gitService, changeLog, backupFile)) {

      if (gitService.getTagList().size() == 0) {
        throw new TagSearchException(
            "No git tag found! You need to run mvn release:prepare command or any other command "
                + "that prepares project for release and applies git version tag to your project");
      }

      DefaultReleaseEntryResolver releaseEntryResolver = new DefaultReleaseEntryResolver();
      Map<VersionTag, List<RevCommit>> tagMap = changelogManager.parse(releaseCount);
      List<ReleaseEntry> entries = releaseEntryResolver.resolve(tagMap);
      changelogManager.writeEntriesToChangelog(entries);

      if (entries.size() > 0 && amendLastCommit) {
        getLog().info("Amending last commit to include changelog changes...");
        new GitVersionManager(gitService).amendLastCommitAndWithChangelog(filename);
      }

    } catch (IOException e) {
      throw new MojoExecutionException("IO exception occurred during mojo execution", e);
    } catch (GitAPIException e) {
      throw new MojoExecutionException(
          "Couldn't locate .git folder in " + projectBaseDir.getAbsolutePath(), e);
    } catch (Exception e) {
      throw new MojoExecutionException("Failed to execute Mojo", e);
    }
  }
}
