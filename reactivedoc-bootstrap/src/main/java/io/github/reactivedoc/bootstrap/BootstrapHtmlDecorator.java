/* (C) Kengo TODA 2021 */
package io.github.reactivedoc.bootstrap;

import com.google.auto.service.AutoService;
import io.github.reactivedoc.api.HtmlDecorator;

@AutoService(HtmlDecorator.class)
public class BootstrapHtmlDecorator implements HtmlDecorator {

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

  private static final String FOOTER = "</body></html>";

  @Override
  public CharSequence decorate(CharSequence html) {
    StringBuilder builder = new StringBuilder(html.length() + HEADER.length() + FOOTER.length());
    return builder.append(HEADER).append(html).append(FOOTER);
  }
}
