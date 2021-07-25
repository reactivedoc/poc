# The ReactiveDoc project Proof of Concept

[![.github/workflows/build.yml](https://github.com/reactivedoc/poc/actions/workflows/build.yml/badge.svg)](https://github.com/reactivedoc/poc/actions/workflows/build.yml)

This is a PoC project for the ReactiveDoc, a documentation builder built on JVM platform.

## Why we still need yet another documentation tool?

We already have several FLOSS projects such as [Sphinx](https://www.sphinx-doc.org/), [Docusaurus](https://docusaurus.io/), and [Honkit](https://github.com/honkit/honkit).
We also have [maven-site-plugin](https://maven.apache.org/plugins/maven-site-plugin/) in Java platform.
These solutions make our documentation easy and productive by following features:

1. Text file based development. We can manage [markdown](https://daringfireball.net/projects/markdown/), [reStructuredText](https://docutils.sourceforge.io/rst.html), [asciidoctor](https://asciidoctor.org/), or other text format to documentation.
  It encourages team to use VCS for communication and automation.
2. Multi-formats output. Tools mainly support HTML, but also PDF, epub, and more format would be supported.
3. Multi-languages support. Writer and translator can work together in one project, or in forked projects.
  It makes sentences that need translation easy to find.
4. Plug-in mechanism. Users in their community can add features easily based on the exposed API.

What we miss here? We think there are several rooms to improve:

1. Integration with JVM platform
    * JVM developer need to manage `node`, `python` or `ruby` only to run documentation tools.
    * JVM developer need to learn a new toolchain to manage dependencies of these tools.
    * Build tool like Maven and Gradle provides less support to run such tools.
2. Fast feedback mechanism
    * Unlike Node.js ecosystem, JVM platform rarely provide `--watch` mechanism.
    * Gradle provides [filesystem watching](https://blog.gradle.org/introducing-file-system-watching) but it provides no integration with such documentation tools.
    * Maven provides no official solution at this timing.

## Core Features of ReactiveDoc

1. Hackable API based on Java standards
    * The core module is enough portable to use from CLI, GUI, Maven, Gradle, or other tools. 
    * Plug-in use pub/sub based on the [Java Flow API](https://community.oracle.com/docs/DOC-1006738).
2. Reactive build
    * Once you change a `.md` file, ReactiveDoc runs build, lint, and more necessary tasks automatic and concurrent.

## What this tool doesn't cover at this moment

This project is still PoC, so it won't cover complicated cases like below:

1. Flexible input format. ReactiveDoc just supports `markdown`.
2. Multi-formats output. ReactiveDoc just supports `.html` file.
3. Multi-languages support. ReactiveDoc just supports English document.
