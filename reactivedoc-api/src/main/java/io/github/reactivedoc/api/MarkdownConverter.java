/* (C) Kengo TODA 2021 */
package io.github.reactivedoc.api;

import io.github.reactivedoc.api.event.HtmlUpdated;
import io.github.reactivedoc.api.event.MarkdownUpdated;
import java.util.concurrent.Flow;

public interface MarkdownConverter extends Flow.Processor<MarkdownUpdated, HtmlUpdated> {}
