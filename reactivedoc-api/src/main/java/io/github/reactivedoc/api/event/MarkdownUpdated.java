/* (C) Kengo TODA 2021 */
package io.github.reactivedoc.api.event;

import java.nio.file.Path;
import java.util.Objects;

/** The event triggered when a markdown file is created or updated. */
public record MarkdownUpdated(Path path, CharSequence markdown) {
  public MarkdownUpdated {
    Objects.requireNonNull(path);
    Objects.requireNonNull(markdown);
    assert path.isAbsolute();
  }
}
