module io.github.reactivedoc.bootstrap {
  requires com.google.auto.service;
  requires io.github.reactivedoc.api;
  requires org.jsoup;

  provides io.github.reactivedoc.api.HtmlDecorator with
      io.github.reactivedoc.bootstrap.BootstrapHtmlDecorator;
}
