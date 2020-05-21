package com.eugenenosenko;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.internal.storage.file.FileRepository;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * Goal which touches a timestamp file.
 *
 * @goal touch
 * @phase process-sources
 */
public class MyMojo extends AbstractMojo {
    public static void main(String[] args) throws IOException {

        try (Git git = new Git(new FileRepository(new File((".git"))))) {

            git.log().call().forEach(commit -> {
                System.out.println("Commit time => " +  commit.getCommitTime());
                Date date = new Date(commit.getCommitTime());
                System.out.println("date = " + date);
                System.out.println("Short message => " +  commit.getShortMessage());
                System.out.println("Full message => " +  commit.getFullMessage());

                System.out.println(commit.getFooterLines());
                System.out.println(commit.getName());
                System.out.println(commit.getId());
                System.out.println(commit.getAuthorIdent());
                System.out.println(commit.getTree());
            });
            System.out.println("git = " + git);
        } catch (NoHeadException e) {
            e.printStackTrace();
        } catch (GitAPIException e) {
            e.printStackTrace();
        }
    }

    enum Type {
        FEAT,
        TEST,
        FIX,
        CHORE,
        REFACTOR,
    }

    static class Change {
        private final Type type;
        private final String commitSha;
        private final String message;
        private final String footer;
        private final String scope;

        Change(Type type, String commitSha, String message, String footer, String scope) {
            this.type = type;
            this.commitSha = commitSha;
            this.message = message;
            this.footer = footer;
            this.scope = scope;
        }
    }

    static class HeaderOneChangeLogEntry extends ChangeLogEntry {

        HeaderOneChangeLogEntry(List<ChangeLogEntry> children) {
            super(children);
        }
    }

    static class HeaderTwoChangeLogEntry extends ChangeLogEntry {
        HeaderTwoChangeLogEntry(List<ChangeLogEntry> children) {
            super(children);
        }
    }

    static class ThirdClassChangeLogEntry extends ChangeLogEntry {

        ThirdClassChangeLogEntry(List<ChangeLogEntry> children) {
            super(children);
        }
    }

    static class ChangeLogEntry {
        private final List<ChangeLogEntry> children;

        protected ChangeLogEntry(List<ChangeLogEntry> children) {
            this.children = children;
        }
    }

    /**
     * Location of the file.
     *
     * @parameter expression="${project.build.directory}"
     * @required
     */
    private File outputDirectory;

    public void execute()
            throws MojoExecutionException {
        File f = outputDirectory;

        if (!f.exists()) {
            f.mkdirs();
        }

        File touch = new File(f, "touch.txt");

        FileWriter w = null;
        try {
            w = new FileWriter(touch);

            w.write("touch.txt");
        } catch (IOException e) {
            throw new MojoExecutionException("Error creating file " + touch, e);
        } finally {
            if (w != null) {
                try {
                    w.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
    }
}
