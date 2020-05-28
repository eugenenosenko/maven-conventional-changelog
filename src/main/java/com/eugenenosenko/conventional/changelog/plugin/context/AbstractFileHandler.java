package com.eugenenosenko.conventional.changelog.plugin.context;

import com.eugenenosenko.conventional.changelog.plugin.file.ConditionalTransformer;
import com.eugenenosenko.conventional.changelog.plugin.file.LogFileHandler;
import com.eugenenosenko.conventional.changelog.plugin.file.NOPTransformer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public abstract class AbstractFileHandler implements LogFileHandler {
  protected final Path file;

  protected AbstractFileHandler(Path file) {
    this.file = file;
  }

  @Override
  public boolean exists() {
    return file.toFile().exists();
  }

  @Override
  public void deleteFile() throws IOException {
    Files.deleteIfExists(file);
  }

  @Override
  public boolean doesFileExist() {
    return file.toFile().exists();
  }

  @Override
  public void clearFileContents() throws IOException {
    Files.write(file, "".getBytes());
  }

  @Override
  public boolean isFileEmpty() {
    return file.toFile().length() == 0;
  }

  @Override
  public void appendStringToFile(String string) throws IOException {
    Files.write(file, string.getBytes(), StandardOpenOption.APPEND);
  }

  @Override
  public String readFirstLine() throws IOException {
    try (BufferedReader reader = Files.newBufferedReader(file)) {
      return reader.readLine();
    }
  }

  @Override
  public void writeFrom(Path source, OpenOption openOption) throws IOException {
    writeFrom(source, openOption, NOPTransformer.INSTANCE);
  }

  @Override
  public void writeFrom(
      Path source, OpenOption openOption, ConditionalTransformer<String> conditionalTransformer)
      throws IOException {

    try (BufferedWriter writer = Files.newBufferedWriter(file, openOption);
        BufferedReader reader = Files.newBufferedReader(source)) {
      String line;

      while ((line = reader.readLine()) != null) {
        line = conditionalTransformer.transform(line);
        writer.write(line);
        writer.newLine();
      }
    }
  }

  @Override
  public Path getFile() {
    return file;
  }
}
