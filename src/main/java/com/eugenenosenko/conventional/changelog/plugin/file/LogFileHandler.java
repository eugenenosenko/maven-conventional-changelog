package com.eugenenosenko.conventional.changelog.plugin.file;

import java.io.IOException;
import java.nio.file.OpenOption;
import java.nio.file.Path;

public interface LogFileHandler {
  boolean isFileEmpty();

  boolean exists();

  void clearFileContents() throws IOException;

  void appendStringToFile(String string) throws IOException;

  String readFirstLine() throws IOException;

  void writeFrom(Path source, OpenOption openOption) throws IOException;

  void writeFrom(
      Path source, OpenOption openOption, ConditionalTransformer<String> conditionalTransformer)
      throws IOException;

  boolean doesFileExist();

  Path getFile();

  void deleteFile() throws IOException;
}
