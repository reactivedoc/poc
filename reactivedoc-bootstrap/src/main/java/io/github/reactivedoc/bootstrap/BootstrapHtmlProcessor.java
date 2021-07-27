/* (C) Kengo TODA 2021 */
package io.github.reactivedoc.bootstrap;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.data.MutableDataSet;
import io.github.reactivedoc.api.MarkdownConverter;
import io.github.reactivedoc.api.event.HtmlUpdated;
import io.github.reactivedoc.api.event.MarkdownUpdated;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Flow;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class BootstrapHtmlProcessor implements MarkdownConverter {
  private final Set<Flow.Subscriber<? super HtmlUpdated>> subscribers = new HashSet<>();
  private final Parser parser;
  private final HtmlRenderer renderer;

  BootstrapHtmlProcessor() {
    MutableDataSet options = new MutableDataSet();
    this.parser = Parser.builder(options).build();
    this.renderer = HtmlRenderer.builder(options).build();
  }

  @Override
  public void subscribe(Flow.Subscriber<? super HtmlUpdated> subscriber) {
    subscribers.add(subscriber);
  }

  @Override
  public void onSubscribe(Flow.Subscription subscription) {}

  @Override
  public void onNext(MarkdownUpdated item) {
    com.vladsch.flexmark.util.ast.Document flexmarkDocument =
        parser.parse(item.markdown().toString());
    String html = renderer.render(flexmarkDocument);
    Document document = Jsoup.parse(html);

    subscribers.forEach(
        subscriber -> {
          subscriber.onNext(new HtmlUpdated(item, document.html()));
        });
  }

  @Override
  public void onError(Throwable throwable) {
    subscribers.forEach(
        subscriber -> {
          subscriber.onError(throwable);
        });
  }

  @Override
  public void onComplete() {}
}
