/* (C) Kengo TODA 2021 */
package io.github.reactivedoc;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class DirectoryWatcher implements AutoCloseable {
  private final WatchService watcher;
  private final Path pathToWatch;

  public DirectoryWatcher(Path pathToWatch) throws IOException {
    this.pathToWatch = Objects.requireNonNull(pathToWatch);
    this.watcher = createWatcher(pathToWatch);
  }

  private static WatchService createWatcher(Path pathToWatch) throws IOException {
    FileSystem fileSystem = pathToWatch.getFileSystem();
    WatchService watcher = fileSystem.newWatchService();
    // TODO watch ENTRY_DELETE
    pathToWatch.register(
        watcher, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY);
    return watcher;
  }

  public Collection<Path> listUpdated() {
    WatchKey key = watcher.poll();
    if (key == null) {
      return Collections.emptyList();
    }
    List<Path> result = new ArrayList<>();
    for (WatchEvent<?> event : key.pollEvents()) {
      WatchEvent.Kind<?> kind = event.kind();
      if (kind == StandardWatchEventKinds.OVERFLOW) {
        continue;
      }
      WatchEvent<Path> ev = (WatchEvent<Path>) event;
      Path filename = pathToWatch.resolve(ev.context()).normalize();
      result.add(filename);
      if (!key.reset()) {
        // TODO throw error?
      }
    }
    return Collections.unmodifiableList(result);
  }

  @Override
  public void close() throws IOException {
    this.watcher.close();
  }
}
