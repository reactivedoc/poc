/* (C) Kengo TODA 2021 */
package io.github.reactivedoc.api;

import io.github.reactivedoc.api.event.HtmlUpdated;
import io.github.reactivedoc.api.event.MarkdownUpdated;
import java.util.function.Function;

@FunctionalInterface
public interface MarkdownConverter extends Function<MarkdownUpdated, HtmlUpdated> {}
