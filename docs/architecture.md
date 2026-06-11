# Architecture

## Overview

Rec follows a layered architecture with a clean separation between the data model, pipeline engine, scripting layer, and AI agent system.

```
┌─────────────────────────────────────────────────────────────┐
│                      User Interfaces                         │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────────┐  │
│  │  JS Scripts   │  │  Agent TUI   │  │  Java API        │  │
│  │  (Rhino)      │  │  (JLine3)    │  │  (direct)        │  │
│  └──────┬───────┘  └──────┬───────┘  └────────┬─────────┘  │
├─────────┼──────────────────┼───────────────────┼────────────┤
│         │         Scripting / Agent Layer        │           │
│  ┌──────▼───────┐  ┌──────▼───────────────────▼─────────┐  │
│  │ rec-scripting│  │            rec-agent                 │  │
│  │ (Rhino, Rec  │  │ (OpenAI, Tools, Skills, MCP, TUI)  │  │
│  │  bridge, App)│  │                                     │  │
│  └──────┬───────┘  └──────────────┬──────────────────────┘  │
├─────────┼──────────────────────────┼────────────────────────┤
│         │         Plugin Layer     │                         │
│  ┌──────▼──────────────────────────▼──────────────────────┐ │
│  │                   RecPlugin (ServiceLoader)              │ │
│  │  ┌────────┐ ┌──────┐ ┌─────────┐ ┌──────┐ ┌─────────┐ │ │
│  │  │ cache  │ │ jdbi │ │reactive │ │ jsonl│ │ parquet │ │ │
│  │  └────────┘ └──────┘ └─────────┘ └──────┘ └─────────┘ │ │
│  └────────────────────────┬────────────────────────────────┘ │
├───────────────────────────┼──────────────────────────────────┤
│                    Core Layer                                 │
│  ┌────────────────────────▼────────────────────────────────┐│
│  │                      rec-core                             ││
│  │  Data Model │ Pipeline (Source/Tee/Target) │ CSV Parser  ││
│  └──────────────────────────────────────────────────────────┘│
└───────────────────────────────────────────────────────────────┘
```

## Module Dependency Graph

```
rec-core (foundation)
  ├── rec-scripting (Rhino JS, App entry, fatJar)
  │     ├── rec-cache         (runtimeOnly)
  │     ├── rec-jdbi          (runtimeOnly)
  │     ├── rec-reactive      (runtimeOnly)
  │     ├── rec-datatype-jsonl  (runtimeOnly)
  │     ├── rec-datatype-parquet (runtimeOnly)
  │     └── rec-agent          (runtimeOnly)
  └── rec-agent (depends on rec-core only)
```

Plugin modules depend on `rec-core` at compile time and are loaded at runtime via `ServiceLoader` in the fat JAR. The `rec-agent` module also depends only on `rec-core`, using reflection to call `Scripting.runfile()` for the ExecuteRecScript tool — avoiding a circular dependency.

## Core Pipeline Model

The pipeline follows a functional streaming pattern:

```
Source → Tee → Tee → ... → Target
```

### Source

`Source` produces a `Stream<DataSet>`. It is the origin of data in the pipeline.

```java
public interface Source {
    Stream<DataSet> stream();
    default Source tee(Tee tee) { ... }
    default Source filter(Predicate<DataSet> pred) { ... }
    default Source skip(long n) { ... }
    default void to(Target target) { ... }
}
```

### Tee

`Tee` transforms or inspects each record passing through. It can also act as a secondary `Source`.

```java
public interface Tee {
    DataSet emit(DataSet dataSet);
    default Source source() { ... }
}
```

### Target

`Target` is the pipeline terminal — it receives processed records.

```java
public interface Target {
    void put(DataSet dataSet);
    default void putAll(Source source) { ... }
}
```

### DataSet

`DataSet` is the universal record interface flowing through the pipeline. It provides type-safe accessors by column index or name.

```java
public interface DataSet {
    Schema schema();
    boolean isNull(int index);
    String getString(int index);
    int getInt(int index);
    long getLong(int index);
    double getDouble(int index);
    boolean getBoolean(int index);
    // ... and more type accessors
}
```

## Plugin System

Plugins are discovered via Java's `ServiceLoader` mechanism.

### RecPlugin Interface

```java
public interface RecPlugin {
    String name();     // JS scope name: __<name>
    Object module();   // Java object exposed to JS
}
```

### Discovery Flow

1. Each plugin module provides `META-INF/services/net.kimleo.rec.plugin.RecPlugin` listing its plugin class
2. The `fatJar` task merges all service files from dependency JARs
3. At startup, `Scripting` scans `ServiceLoader.load(RecPlugin.class)` and installs each plugin
4. In JS, plugins are merged into the `rec` object (e.g., `rec.query()`, `rec.createAgent()`) via `require("rec")`

## Data Model

### Schema

Immutable column name-to-index mapping with optional typed columns:

```java
Schema schema = Schema.of("name", "age", "email");         // all STRING
Schema typed = Schema.typed(
    new String[]{"id", "price"},
    new DataType[]{DataType.INT, DataType.DOUBLE}
);
```

### DataType

Supported types: `STRING`, `INT`, `LONG`, `FLOAT`, `DOUBLE`, `BOOLEAN`, `BYTES`, `DECIMAL`, `DATE`, `TIMESTAMP`.

### DataFrame

Column-oriented table storage using `ColumnVector` arrays. Provides:
- `row(i)` → `DataRow` (single-row view)
- `rows()` → `Stream<DataSet>` (streaming access)
- `Builder` for row-by-row construction

### DataRow

Lightweight single-row view into a `DataFrame`. This is the typical record type flowing through pipelines.

## Build System

### Gradle Structure

```
rec/                          (root project)
├── build.gradle              (shared config: java, checkstyle, jacoco)
├── settings.gradle           (module list)
├── config/checkstyle/        (checkstyle rules)
├── rec-core/
├── rec-scripting/            (fatJar task)
├── rec-agent/
├── rec-cache/
├── rec-jdbi/
├── rec-reactive/
├── rec-datatype-jsonl/
└── rec-datatype-parquet/
```

### Checkstyle Rules

- Max line length: 113 characters
- No star imports
- Max method length: 30 lines
- Max constructor length: 15 lines
- Constant naming conventions
- Empty block detection

### Fat JAR

The `rec-scripting:fatJar` task produces a single executable JAR:

1. `mergeServiceFiles` — collects and merges all `META-INF/services` from dependencies
2. `fatJar` — bundles all runtime classpath JARs, excludes signatures and duplicate services
3. Output: `rec-scripting/build/libs/rec-core-0.2.2-all.jar`
4. Main class: `net.kimleo.rec.App`

### `./rec` Shell Script

The `./rec` script provides the CLI entry point:

```bash
./rec script pipeline.js     # Run a JS pipeline
./rec --agent                # Start agent TUI
./rec --build                # Force rebuild fat JAR
./rec dump file.bin 4        # Dump binary cache file
```

If the fat JAR is missing, the script auto-triggers `./gradlew :rec-scripting:fatJar`.
