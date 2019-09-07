package org.ananas.runner.steprunner.subprocess.utils;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.ananas.runner.steprunner.subprocess.SubProcessConfiguration;
import org.apache.beam.sdk.io.FileSystems;
import org.apache.beam.sdk.io.fs.ResolveOptions.StandardResolveOptions;
import org.apache.beam.sdk.io.fs.ResourceId;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Utilities for dealing with movement of files from object stores and workers. */
public class FileUtils {

  private static final Logger LOG = LoggerFactory.getLogger(FileUtils.class);

  public static ResourceId getFileResourceId(String directory, String fileName) {
    ResourceId resourceID = FileSystems.matchNewResource(directory, true);
    return resourceID.getCurrentDirectory().resolve(fileName, StandardResolveOptions.RESOLVE_FILE);
  }

  public static String toStringParams(ProcessBuilder builder) {
    return String.join(",", builder.command());
  }

  public static String copyFileFrom(String sourcePath, String targetPath, String fileName)
      throws Exception {

    if (fileName == null) {
      throw new IllegalArgumentException("FileName can not be null.");
    }

    ResourceId sourceFile = getFileResourceId(sourcePath, fileName);

    LOG.info("Copying file from " + sourcePath);

    ResourceId destinationFile = getFileResourceId(targetPath, fileName);
    // FileSystems.copy(ImmutableList.of(sourceFile), ImmutableList.of(destinationFile));
    try {
      return copyFile(sourceFile, destinationFile);
    } catch (Exception ex) {
      LOG.error(
          String.format("Error copying file from %s  to %s", sourceFile, destinationFile), ex);
      throw ex;
    }
  }

  public static String copyFile(ResourceId sourceFile, ResourceId destinationFile)
      throws IOException {

    try (WritableByteChannel writeChannel = FileSystems.create(destinationFile, "text/plain")) {
      try (ReadableByteChannel readChannel = FileSystems.open(sourceFile)) {

        final ByteBuffer buffer = ByteBuffer.allocateDirect(16 * 1024);
        while (readChannel.read(buffer) != -1) {
          buffer.flip();
          writeChannel.write(buffer);
          buffer.compact();
        }
        buffer.flip();
        while (buffer.hasRemaining()) {
          writeChannel.write(buffer);
        }
      }
    }

    return destinationFile.toString();
  }

  /**
   * Create directories needed based on configuration.
   *
   * @param configuration
   * @throws IOException
   */
  public static void createDirectoriesOnWorker(SubProcessConfiguration configuration)
      throws IOException {

    try {

      Path path = Paths.get(configuration.getWorkerPath());

      if (!path.toFile().exists()) {
        Files.createDirectories(path);
        LOG.info(String.format("Created Folder %s ", path.toFile()));
      }
    } catch (FileAlreadyExistsException ex) {
      LOG.warn(
          String.format(
              " Tried to create folder %s which already existsed, this should not happen!",
              configuration.getWorkerPath()),
          ex);
    }
  }

  public static String readLineOfLogFile(InputStream stream) {

    try (LineIterator br = IOUtils.lineIterator(stream, "UTF-8")) {
      return br.next();
    } catch (IOException e) {
      LOG.error("Error reading the first line of file", e);
    }

    // `return empty string rather than NULL string as this data is often used in further logging
    return "";
  }
}
