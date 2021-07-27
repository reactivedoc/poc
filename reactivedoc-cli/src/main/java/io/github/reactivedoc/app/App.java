/* (C) Kengo TODA 2021 */
package io.github.reactivedoc.app;

import io.github.reactivedoc.DirectoryWatcher;
import io.github.reactivedoc.api.MarkdownConverter;
import io.github.reactivedoc.api.event.MarkdownUpdated;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.TimeoutStream;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.cli.CLI;
import io.vertx.core.cli.CommandLine;
import io.vertx.core.cli.Option;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class App implements AutoCloseable {
  private static final int STATUS_NORMAL = 0;
  private static final int STATUS_INVALID_OPTION = 1;
  private static final Logger logger = LogManager.getLogger(App.class);

  public static void main(String[] args) throws IOException {
    CLI cli = createCli();
    CommandLine commandLine = cli.parse(Arrays.asList(args));

    if (!commandLine.isValid() || commandLine.isAskingForHelp()) {
      StringBuilder builder = new StringBuilder();
      cli.usage(builder);
      System.err.println(builder);
      System.exit(commandLine.isValid() ? STATUS_NORMAL : STATUS_INVALID_OPTION);
    }

    Path pathToWatch =
        Paths.get(commandLine.getOptionValue("source").toString()).normalize().toAbsolutePath();
    // TODO validate if it contains ".."
    logger.info("source directory is {}", pathToWatch);
    if (!pathToWatch.toFile().isDirectory()) {
      System.err.print("source directory does not exist: ");
      System.err.println(pathToWatch);
      System.exit(STATUS_INVALID_OPTION);
    }

    Path pathToOutput =
        Paths.get(commandLine.getOptionValue("output").toString()).normalize().toAbsolutePath();
    // TODO validate if it contains ".."
    logger.info("output directory is {}", pathToOutput);
    if (!pathToOutput.toFile().isDirectory() && !pathToOutput.toFile().mkdirs()) {
      System.err.print("failed to create output directory: ");
      System.err.println(pathToOutput);
      System.exit(STATUS_INVALID_OPTION);
    }

    App app = new App(pathToWatch, pathToOutput);
    if (commandLine.isFlagEnabled("watch")) {
      app.watch().endHandler(v -> app.close());
    } else {
      app.batch()
          .onComplete(
              result -> {
                if (result.failed()) {
                  logger.catching(result.cause());
                }
                app.close();
              });
    }
  }

  private final Path pathToWatch;
  private final Path pathToOutput;
  private final Vertx vertx;
  private final MarkdownConverter converter;

  App(Path pathToWatch, Path pathToOutput) {
    this.pathToWatch = Objects.requireNonNull(pathToWatch);
    this.pathToOutput = Objects.requireNonNull(pathToOutput);
    this.vertx = Vertx.vertx();
    this.converter = ServiceLoader.load(MarkdownConverter.class).findFirst().orElseThrow();
  }

  private static String replaceFileExt(String filename) {
    assert filename.endsWith(".md");
    return filename.substring(0, filename.length() - 3) + ".html";
  }

  private static CLI createCli() {
    CLI cli =
        CLI.create("reactivedoc")
            .addOption(
                new Option()
                    .setLongName("source")
                    .setShortName("s")
                    .setDescription("the directory to parse/monitor files")
                    .setDefaultValue("docs"))
            .addOption(
                new Option()
                    .setLongName("output")
                    .setShortName("o")
                    .setDescription("the directory to output files")
                    .setRequired(true))
            .addOption(
                new Option()
                    .setLongName("help")
                    .setShortName("h")
                    .setDescription("print the usage of this command")
                    .setHelp(true)
                    .setFlag(true))
            .addOption(
                new Option()
                    .setLongName("watch")
                    .setShortName("w")
                    .setDescription("watch file changes")
                    .setFlag(true));
    return cli;
  }

  private TimeoutStream watch() throws IOException {
    DirectoryWatcher watcher = new DirectoryWatcher(pathToWatch);

    return vertx
        .periodicStream(100)
        .handler(
            timerId -> {
              watcher.listUpdated().forEach(this::build);
            })
        .exceptionHandler(t -> logger.error("Failed to handle the periodic task", t))
        .endHandler(
            v -> {
              try {
                watcher.close();
              } catch (IOException e) {
                logger.warn("Failed to close filesystem watcher", e);
              }
            });
  }

  private CompositeFuture batch() throws IOException {
    List<Future> futures = new ArrayList<>();
    Files.walkFileTree(
        pathToWatch,
        new SimpleFileVisitor<Path>() {
          @Override
          public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) {
            if (path.toFile().canRead()) {
              futures.add(build(path));
            }

            return FileVisitResult.CONTINUE;
          }
        });
    return CompositeFuture.all(futures);
  }

  private Future<Void> build(Path source) {
    if (!source.startsWith(pathToWatch)) {
      throw new IllegalArgumentException("%s is not a file in %s".formatted(source, pathToWatch));
    }

    String filename = source.toString();
    Path destDir = pathToOutput.resolve(pathToWatch.relativize(source)).getParent();
    String destFile = destDir.resolve(replaceFileExt(source.getFileName().toString())).toString();
    if (filename.endsWith(".md")) {
      logger.info("build {} and save it to {}", source, destFile);
      Future<MarkdownUpdated> future = vertx
              .fileSystem()
              .readFile(filename)
              .map(buffer -> buffer.toString())
              .map(markdown -> new MarkdownUpdated(source, markdown))
        .map(event -> Future.future(promise -> {
          converter.onNext(event);

        }))
              .map(charSequence -> Buffer.buffer(charSequence.toString()))
              .flatMap(html -> vertx.fileSystem().writeFile(destFile, html))
              .onSuccess(v -> logger.debug("write HTML to {} successfully", destFile))
              .onFailure(Throwable::printStackTrace);
    } else {
      logger.debug("{} is not MD file", filename);
      return Future.succeededFuture();
    }
  }

  @Override
  public void close() {
    vertx.close(
        result -> {
          if (result.succeeded()) {
            logger.info("vertx instance has been closed successfully");
          } else {
            logger.warn("vertx instance cannot be closed successfully", result.cause());
          }
        });
  }
}
