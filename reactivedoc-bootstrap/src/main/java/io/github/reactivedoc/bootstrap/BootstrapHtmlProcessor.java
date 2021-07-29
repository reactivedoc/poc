/* (C) Kengo TODA 2021 */
package io.github.reactivedoc.bootstrap;

import com.google.auto.service.AutoService;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.data.MutableDataSet;
import io.github.reactivedoc.api.MarkdownConverter;
import io.github.reactivedoc.api.event.HtmlUpdated;
import io.github.reactivedoc.api.event.MarkdownUpdated;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

@AutoService(MarkdownConverter.class)
public class BootstrapHtmlProcessor implements MarkdownConverter {
  private final Parser parser;
  private final HtmlRenderer renderer;

  public BootstrapHtmlProcessor() {
    MutableDataSet options = new MutableDataSet();
    this.parser = Parser.builder(options).build();
    this.renderer = HtmlRenderer.builder(options).build();
  }

  @Override
  public HtmlUpdated apply(MarkdownUpdated markdownUpdated) {
    com.vladsch.flexmark.util.ast.Document flexmarkDocument =
        parser.parse(markdownUpdated.markdown().toString());
    String html = renderer.render(flexmarkDocument);
    return new HtmlUpdated(markdownUpdated, decorate(html));
  }

  // TODO add a <title>
  // TODO manage lang prop of the <html> element
  // TODO add <link rel="stylesheet"> elements
  private static final String HEADER =
      """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                <meta charset="UTF-8">
                <meta http-equiv="X-UA-Compatible" content="IE=edge">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <meta name="generator" content="reactivedoc 0.1.0">
                <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/3.4.1/css/bootstrap.min.css" integrity="sha384-HSMxcRTRxnN+Bdg0JdbxYKrThecOKuH5zCYotlSAcp1+c8xmyTe9GYg1l9a69psu" crossorigin="anonymous">
                <script async src="https://stackpath.bootstrapcdn.com/bootstrap/3.4.1/js/bootstrap.min.js" integrity="sha384-aJ21OjlMXNL5UyIl/XNwTMqvzeRMZH2w8c5cRVpzpU8Y5bApTppSuUkhZXN0VxHd" crossorigin="anonymous"></script>
                </head>
                <body>
                """;

  private String decorate(String htmlBody) {
    Document document = Jsoup.parse(HEADER.concat(htmlBody).concat("</body>"));
    return document.toString();
  }
}
