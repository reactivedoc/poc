/* (C) Kengo TODA 2021 */
package io.github.reactivedoc.api.event;

import java.util.Objects;

public record HtmlUpdated(MarkdownUpdated source, CharSequence html) {
  public HtmlUpdated {
    Objects.requireNonNull(source);
    Objects.requireNonNull(html);
  }
}
