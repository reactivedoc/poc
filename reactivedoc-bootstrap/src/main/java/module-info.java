module io.github.reactivedoc.bootstrap {
  requires com.google.auto.service;
  requires io.github.reactivedoc.api;

  provides io.github.reactivedoc.api.HtmlDecorator with
      io.github.reactivedoc.bootstrap.BootstrapHtmlDecorator;
}
