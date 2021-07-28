module io.github.reactivedoc.core {
  requires io.github.reactivedoc.api;
  requires org.apache.logging.log4j;
  requires io.vertx.core;

  uses io.github.reactivedoc.api.HtmlDecorator;

  exports io.github.reactivedoc;
  exports io.github.reactivedoc.markdown;
}
