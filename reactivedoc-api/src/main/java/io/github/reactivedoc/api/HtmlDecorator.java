/* (C) Kengo TODA 2021 */
package io.github.reactivedoc.api;

public interface HtmlDecorator {
  /**
   * A non-blocking function that decorates or updates given HTML
   *
   * @param html HTML given from the previous decorator/process
   * @return decorated HTML
   */
  // TODO consider to treat HTML as DOM tree
  CharSequence decorate(CharSequence html);
}
