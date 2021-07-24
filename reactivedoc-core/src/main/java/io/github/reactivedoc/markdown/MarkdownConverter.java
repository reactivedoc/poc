/* (C) Kengo TODA 2021 */
package io.github.reactivedoc.markdown;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.MutableDataSet;
import io.github.reactivedoc.api.HtmlDecorator;
import io.vertx.core.buffer.Buffer;
import java.util.List;
import java.util.ServiceLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MarkdownConverter {
  private static final Logger logger = LogManager.getLogger(MarkdownConverter.class);

  private final Parser parser;
  private final HtmlRenderer renderer;
  private final List<HtmlDecorator> decorators;

  public MarkdownConverter() {
    MutableDataSet options = new MutableDataSet();
    this.parser = Parser.builder(options).build();
    this.renderer = HtmlRenderer.builder(options).build();
    this.decorators =
        ServiceLoader.load(HtmlDecorator.class).stream().map(ServiceLoader.Provider::get).toList();
  }

  public CharSequence convert(Buffer markdown) {
    logger.traceEntry("parse markdown");
    Node document = parser.parse(markdown.toString());
    logger.traceExit();
    logger.traceEntry("html rendering");
    CharSequence html = renderer.render(document);
    logger.traceExit();
    for (HtmlDecorator decorator : decorators) {
      logger.traceEntry("decorating html with %s", decorator.getClass());
      html = decorator.decorate(html);
      logger.traceExit();
    }
    return html;
  }
}
