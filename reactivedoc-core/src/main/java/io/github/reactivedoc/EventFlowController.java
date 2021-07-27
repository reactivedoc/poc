package io.github.reactivedoc;

import io.github.reactivedoc.api.MarkdownConverter;
import io.github.reactivedoc.api.event.MarkdownUpdated;
import io.vertx.core.Vertx;
import io.vertx.core.streams.Pump;
import io.vertx.core.streams.ReadStream;
import io.vertx.core.streams.WriteStream;
import io.vertx.ext.reactivestreams.ReactiveReadStream;
import io.vertx.ext.reactivestreams.ReactiveWriteStream;

import java.nio.file.Path;
import java.util.ServiceLoader;

public class EventFlowController {
    private final MarkdownConverter converter;
    private final Vertx vertx;

    public EventFlowController(Vertx vertx) {
        this.vertx = vertx;
        this.converter = ServiceLoader.load(MarkdownConverter.class).findFirst().orElseThrow();
    }

    public void run(ReadStream<MarkdownUpdated> source, Path outDir) {
        ReactiveWriteStream<MarkdownUpdated> rws = ReactiveWriteStream.writeStream(vertx);
        rws.subscribe(converter);
        source.pipeTo(converter)
    }
}
