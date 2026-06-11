# rec-core — Foundation Module

The core module provides the data model, pipeline abstractions, CSV parsing, and the plugin interface. It has zero dependencies beyond SLF4J.

## Packages

### `net.kimleo.rec.data` — Data Model

#### DataSet

The universal record interface. All data flowing through pipelines implements this.

```java
public interface DataSet {
    Schema schema();
    boolean isNull(int index);
    String getString(int index);
    int getInt(int index);
    long getLong(int index);
    float getFloat(int index);
    double getDouble(int index);
    boolean getBoolean(int index);
    byte[] getBytes(int index);
    BigDecimal getDecimal(int index);
    LocalDate getDate(int index);
    Instant getTimestamp(int index);
}
```

Accessors are also available by column name: `getString("name")`, `getInt("age")`, etc.

#### Schema

Immutable column name-to-index mapping.

```java
// Untyped (all STRING)
Schema schema = Schema.of("name", "age", "email");

// Typed columns
Schema typed = Schema.typed(
    new String[]{"id", "price", "active"},
    new DataType[]{DataType.INT, DataType.DOUBLE, DataType.BOOLEAN}
);

// Query schema
schema.columnCount();       // 3
schema.index("age");        // 1
schema.name(0);             // "name"
schema.type(1);             // DataType.STRING (or specific type)
```

#### DataType

Enum of supported column types:

| Type | Java Type | Default Value |
|------|-----------|---------------|
| `STRING` | `String` | `""` |
| `INT` | `int` | `0` |
| `LONG` | `long` | `0L` |
| `FLOAT` | `float` | `0.0f` |
| `DOUBLE` | `double` | `0.0` |
| `BOOLEAN` | `boolean` | `false` |
| `BYTES` | `byte[]` | `new byte[0]` |
| `DECIMAL` | `BigDecimal` | `BigDecimal.ZERO` |
| `DATE` | `LocalDate` | `LocalDate.MIN` |
| `TIMESTAMP` | `Instant` | `Instant.EPOCH` |

`DataType.infer(value)` infers the type from a string value.

#### DataFrame

Column-oriented table storage. The primary container for tabular data.

```java
// Build a DataFrame
Schema schema = Schema.of("name", "age");
DataFrame df = DataFrame.builder(schema)
    .addRow("Alice", "30")
    .addRow("Bob", "25")
    .build();

// Access data
df.rowCount();              // 2
df.schema();                // Schema
DataRow row = df.row(0);    // First row
row.getString(0);           // "Alice"

// Stream all rows
df.rows().forEach(row -> {
    System.out.println(row.getString("name"));
});
```

#### DataRow

Lightweight single-row view into a DataFrame. Implements `DataSet`.

#### ColumnVector

Column-oriented storage using typed primitive arrays (`int[]`, `double[]`, etc.) with a parallel `boolean[]` null bitmap. Efficient for analytical workloads.

### `net.kimleo.rec.model` — Pipeline Abstractions

#### Source

```java
public interface Source {
    Stream<DataSet> stream();

    // Chain a Tee into the pipeline
    default Source tee(Tee tee);

    // Filter records
    default Source filter(Predicate<DataSet> predicate);

    // Skip first N records
    default Source skip(long n);

    // Consume all records into a Target
    default void to(Target target);
}
```

#### Tee

```java
public interface Tee {
    // Process a record (transform or inspect)
    DataSet emit(DataSet dataSet);

    // Convert accumulated data back to a Source
    default Source source();
}
```

#### Target

```java
public interface Target {
    void put(DataSet dataSet);
    default void putAll(Source source);
}
```

### `net.kimleo.rec.model.impl` — Built-in Implementations

| Class | Type | Description |
|-------|------|-------------|
| `CSVFileSource` | Source | Read CSV files with configurable delimiter |
| `CSVSource` | Source | Read CSV from any `Reader` |
| `FlatFileTarget` | Target | Write CSV output |
| `CollectTee` | Tee | Accumulate records into a `List<DataSet>` |
| `ItemCounterTee` | Tee | Count records matching a predicate |
| `TransformTee` | Tee | Apply `Function<DataSet, DataSet>` |

### `net.kimleo.rec.sepval` — CSV/Delimiter Parsing

`SimpleParser` is a hand-written, high-performance CSV parser supporting:

- Configurable delimiter character
- Quoted fields (double-quote escaping)
- `ParseConfig` for parser configuration

```java
SimpleParser parser = new SimpleParser(new ParseConfig(','));
ParseResult result = parser.parse("Alice,30,\"New York, NY\"");
String[] values = result.getValues().toArray(new String[0]);
// ["Alice", "30", "New York, NY"]
```

### `net.kimleo.rec.plugin` — Plugin Interface

```java
public interface RecPlugin {
    String name();     // Module name for JS scope
    Object module();   // Java object whose methods become JS functions
}
```

See [Extending Rec](../extending.md) for how to create plugins.

### `net.kimleo.rec.execution` — Execution Context

Supports restartable pipelines with count-based checkpointing:

- `ExecutionContext` — Interface for `commit()`, `persist()`, `restart()`
- `RestartableSource` — Source that catches errors and persists context

### `net.kimleo.rec.accessor` — Field Accessor System

Legacy field-name mapping system:

- `Accessor` — Maps field names to positions
- `RecordWrapper` — Wraps raw data with named field access
- `Accessible<T>` — Positional access interface
- `Mapped<T>` — Named field access interface

### `net.kimleo.rec.common` — Utilities

- `Pair<A, B>` / `Tuple` — Generic containers
- `LinkedMultiHashMap` / `MultiMap` — Multi-value maps
- `Assert` — Assertion utilities
- Exception types: `InitializationException`, `RecException`

### `net.kimleo.rec.utils` — Utilities

- `Records` — Binary file dump utilities
