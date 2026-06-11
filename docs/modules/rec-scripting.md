# rec-scripting — JavaScript Scripting Engine

The scripting module provides a Rhino-based JavaScript runtime for building data pipelines. It serves as the primary user-facing interface and produces the fat JAR.

## Overview

Rec scripting bridges Java pipeline abstractions into a JavaScript API. Scripts use `require("rec")` to access the full pipeline toolkit.

## Entry Point: `App`

```
net.kimleo.rec.App
```

### CLI Commands

```bash
./rec script pipeline.js      # Run a JS pipeline script
./rec js pipeline.js          # Alias for 'script'
./rec --agent                 # Start the agent TUI
./rec --agent --model gpt-4o  # With custom model
./rec dump file.bin 4         # Dump binary cache (4-byte columns)
./rec pipeline.js             # Shorthand (single arg = script)
```

### Options

| Flag | Description |
|------|-------------|
| `--retry <file>` | Retry from a persisted execution context |
| `--build` | Force rebuild of fat JAR before running |

## JavaScript API

### `require("rec")`

The main module provides the full pipeline API:

```javascript
var rec = require("rec");
```

### Source Factories

#### `rec.csv(reader, delimiter, accessor)`

Create a CSV source from a reader.

```javascript
var source = rec.csv(rec.file("data.csv"), ",", "name,age,city");
```

- `reader` — A `java.io.Reader` or result of `rec.file()`
- `delimiter` — Column delimiter string (e.g., `","`, `"\t"`)
- `accessor` — Comma-separated column names

#### `rec.file(path)`

Open a file for reading.

```javascript
var reader = rec.file("input.csv");
```

### Pipeline Operations

#### `rec.action(fn)`

Create a Tee that applies a transformation function.

```javascript
var upper = rec.action(function(row) {
  return row;  // return transformed row
});
source.tee(upper).to(target);
```

#### `rec.pred(fn)`

Create a predicate for filtering.

```javascript
var isAdult = rec.pred(function(row) {
  return row.getInt(1) >= 18;
});
source.filter(isAdult).to(target);
```

#### `rec.stateful(init, fn)`

Create a stateful Tee with an accumulator.

```javascript
var runningSum = rec.stateful(
  function() { return { total: 0 }; },
  function(state, row) {
    state.total += row.getDouble(2);
    print("Running total: " + state.total);
    return row;
  }
);
```

### Targets

#### `rec.flat(path)`

Write CSV output to a file.

```javascript
source.to(rec.flat("output.csv"));
```

#### `rec.target(fn)`

Create a custom target from a function.

```javascript
source.to(rec.target(function(row) {
  print("Received: " + row.getString(0));
}));
```

#### `rec.dummy()`

Discard all records (useful with counting/collection tees).

```javascript
source.to(rec.dummy());
```

### Utility Tees

#### `rec.counter(fn)`

Count records matching a predicate.

```javascript
var counter = rec.counter(function(row) {
  return row.getString(2) === "active";
});
source.tee(counter).to(rec.dummy());
print("Active: " + counter.count());
```

#### `rec.collect()`

Collect all records into a list.

```javascript
var collector = rec.collect();
source.tee(collector).to(rec.dummy());
var records = collector.list();
```

#### `rec.cache(bufferSize)`

Cache records to a binary temp file for replay.

```javascript
source.tee(rec.cache(4096)).tee(expensiveOp).to(target);
```

### Module System

The scripting engine supports `require()` for loading JS modules:

```javascript
var utils = require("./lib/utils");
var transform = require("transforms/normalize");
```

Resolution order:
1. Relative paths (`./`, `../`) — resolved from the script's directory
2. Classpath resources (`rec/` prefix)
3. Bare module names — resolved from the classpath

## Scripting Internals

### Scripting Engine (`Scripting`)

The `Scripting` class initializes the Rhino runtime:

1. Creates a Rhino `Context` with optimization level 9
2. Installs `__rec` (Rec bridge) and `__core` (CoreRecPlugin)
3. Discovers all `RecPlugin` implementations via `ServiceLoader`
4. Installs each plugin as `__<name>` in the JS scope
5. Sets up the `require()` module resolver

### Rec Bridge (`Rec`)

The `Rec` class provides Java-to-JS bridge utilities:

- `action(fn)` — Wrap a JS function as a `TransformTee`
- `pred(fn)` — Wrap a JS function as a `Predicate<DataSet>`
- `stateful(init, fn)` — Create a stateful Tee
- `wrapObject(obj)` — Wrap a Java object for JS access
- `file(path)` — Open a file reader
- `flat(path)` — Create a `FlatFileTarget`
- `cache(size)` — Create a `BufferedCachingTee`

### JS Record (`JSRecord`)

`JSRecord` implements both `DataSet` (Java) and `Scriptable` (Rhino), allowing records to be accessed naturally from JS:

```javascript
row.name       // access by field name
row[0]         // access by index
row.getInt(1)  // typed accessor
```

### Execution Context

Supports restartable pipelines with `NativeExecutionContext`:

```javascript
// Pipeline with retry support
rec.restartable(source, function(ctx) {
  source.tee(transform).to(target);
  ctx.commit();
});
```

## Fat JAR Build

The `fatJar` Gradle task produces a single executable JAR:

```bash
./gradlew :rec-scripting:fatJar
```

### Build Process

1. **mergeServiceFiles** — Collects `META-INF/services` from all dependency JARs and merges them
2. **fatJar** — Bundles all runtime classpath entries, excludes:
   - `META-INF/services/**` (replaced by merged version)
   - `META-INF/*.SF`, `*.DSA`, `*.RSA` (signature files)
3. **Output** — `rec-scripting/build/libs/rec-core-0.2.2-all.jar`
4. **Main class** — `net.kimleo.rec.App`

### Configuration

The fat JAR uses `zip64 = true` to support the large number of entries from all dependencies.
