/* (C) Kengo TODA 2021 */
package io.github.reactivedoc;

import io.github.reactivedoc.api.MarkdownConverter;
import io.github.reactivedoc.api.event.HtmlUpdated;
import io.github.reactivedoc.api.event.MarkdownUpdated;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import java.nio.file.Path;
import java.util.ServiceLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EventFlowController {
  private final MarkdownConverter converter;
  private final Vertx vertx;
  private final Logger logger = LogManager.getLogger(EventFlowController.class);

  public EventFlowController(Vertx vertx) {
    this.vertx = vertx;
    this.converter = ServiceLoader.load(MarkdownConverter.class).findFirst().orElseThrow();
  }

  public Future<Void> run(MarkdownUpdated source, Path sourceDir, Path outDir) {
    HtmlUpdated htmlUpdated = converter.apply(source);
    Path destDir = outDir.resolve(sourceDir.relativize(source.path())).getParent();
    Path destFile = destDir.resolve(replaceFileExt(source.path().getFileName().toString()));
    return vertx.fileSystem().writeFile(destFile.toString(), Buffer.buffer(htmlUpdated.html()));
  }

  private static String replaceFileExt(String filename) {
    assert filename.endsWith(".md");
    return filename.substring(0, filename.length() - 3) + ".html";
  }
}
