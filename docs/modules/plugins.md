# Plugin Modules

Rec uses a `ServiceLoader`-based plugin architecture. All plugin modules implement `RecPlugin` and are automatically discovered when bundled in the fat JAR.

---

## rec-cache

Binary and in-memory caching for pipeline records.

### Overview

Provides `Tee` implementations that cache records to avoid reprocessing expensive transformations.

### Components

| Class | Description |
|-------|-------------|
| `BufferedCachingTee` | Caches records to a binary temp file with configurable buffer size |
| `InMemoryCacheTee` | Caches records in memory (list-based) |
| `RedisCacheTee` | Distributed caching via Redis |

### Usage (JS)

```javascript
var rec = require("rec");

// Binary cache with 4KB buffer
source.tee(rec.cache(4096)).tee(expensiveOp).to(target);

// The cache stores records in a temp file
// On replay (from a RestartableSource), cached records are read back
```

### Usage (Java)

```java
BufferedCachingTee cache = new BufferedCachingTee(4096, schema);
source.tee(cache).tee(expensiveOp).to(target);

// Replay from cache
Source replay = cache.source();
```

### Design

`BufferedCachingTee` serializes records to a binary format using `ByteBuffer`:
- Each record's values are written as length-prefixed byte arrays
- The buffer is flushed to disk when full
- `source()` returns a new `Source` that deserializes from the cached binary file

### CachePlugin

The plugin also provides `CachePlugin` interface for `rec.cache(bufferSize)`:

```java
public interface CachePlugin {
    Tee cache(int bufferSize);
}
```

---

## rec-jdbi

Database source using JDBI3.

### Overview

Provides a `Source` implementation that reads from relational databases via JDBI3.

### Components

| Class | Description |
|-------|-------------|
| `JdbiSource` | Source backed by a JDBI3 `Handle` + SQL query |
| `ResultSetSource` | Source backed by a raw JDBC `ResultSet` |
| `MapRecord` | `DataSet` wrapper for `Map<String, Object>` results |
| `ResultSetMapper` | Converts `ResultSet` rows to `MapRecord` |

### Usage (Java)

```java
import net.kimleo.rec.model.impl.JdbiSource;

JdbiSource source = JdbiSource.builder()
    .url("jdbc:postgresql://localhost:5432/mydb")
    .username("user")
    .password("pass")
    .query("SELECT id, name, price FROM products WHERE active = true")
    .build();

source.stream().forEach(row -> {
    System.out.println(row.getString("name") + ": " + row.getDouble("price"));
});
```

### Usage (JS)

```javascript
var source = __jdbi.query(
  "jdbc:postgresql://localhost:5432/mydb",
  "user", "pass",
  "SELECT id, name, price FROM products"
);

source.to(rec.flat("products.csv"));
```

### Dependencies

- `org.jdbi:jdbi3-core`
- `org.postgresql:postgresql` (PostgreSQL driver)

---

## rec-reactive

Push-based reactive pipeline.

### Overview

Implements a push-based pipeline model where data is pushed through a chain of reactive operators, rather than pulled from a stream.

### Components

| Class | Description |
|-------|-------------|
| `ReactiveTee` | Core reactive tee — implements both `Tee` and `Source`, supports chained operators |

### Usage (Java)

```java
import net.kimleo.rec.model.impl.ReactiveTee;

ReactiveTee pipeline = ReactiveTee.create()
    .filter(row -> row.getInt("age") > 18)
    .tee(inspector)
    .skip(10);

pipeline.bind(target);

// Push data through the reactive pipeline
source.stream().forEach(pipeline::emit);
```

### Usage (JS)

```javascript
var reactive = __reactive.chain();
reactive.filter(rec.pred(function(row) {
  return row.getInt(1) > 18;
}));
reactive.bind(rec.target(function(row) {
  print("Adult: " + row.getString(0));
}));

source.to(rec.target(function(row) {
  reactive.emit(row);
}));
```

### Design

`ReactiveTee` maintains a chain of operators:
- `filter(Predicate)` — Only emit records matching the predicate
- `tee(Tee)` — Apply a transformation/inspection stage
- `skip(long)` — Skip first N records
- `bind(Target)` — Connect the end of the chain to a target

Data pushed via `emit(DataSet)` flows through the chain and arrives at the bound target.

---

## rec-datatype-jsonl

JSON Lines (JSONL) reader and writer.

### Overview

Provides `Source` and `Target` implementations for the JSON Lines format, where each line is a valid JSON object.

### Components

| Class | Description |
|-------|-------------|
| `JsonlSource` | Read JSON Lines files, each line becomes a `DataSet` |
| `JsonlTarget` | Write `DataSet` records as JSON Lines |

### Input Format

```json
{"name": "Alice", "age": 30, "city": "NYC"}
{"name": "Bob", "age": 25, "city": "LA"}
```

### Usage (JS)

```javascript
var rec = require("rec");
var source = rec.jsonl(rec.file("data.jsonl"));
source.to(rec.flat("output.csv"));

var target = rec.jsonlTarget(new File("results.jsonl"));
source.to(target);
```

### Usage (Java)

```java
JsonlSource source = new JsonlSource(Paths.get("data.jsonl"));
source.stream().forEach(row -> {
    System.out.println(row.getString("name"));
});

JsonlTarget target = new JsonlTarget(Paths.get("output.jsonl"));
source.to(target);
```

### Schema Inference

Schema is inferred from the JSON keys of the first record. All values are stored as `STRING` type.

### Dependencies

- `com.fasterxml.jackson.core:jackson-databind` (Jackson 2.x)

---

## rec-datatype-parquet

Apache Parquet reader and writer.

### Overview

Provides `Source` and `Target` implementations for the Apache Parquet columnar storage format.

### Components

| Class | Description |
|-------|-------------|
| `ParquetSource` | Read Parquet files using `ParquetReader` |
| `ParquetTarget` | Write Parquet files using `ParquetWriter` |

### Usage (JS)

```javascript
var rec = require("rec");
var source = rec.parquet(new File("data.parquet"));
source.to(rec.flat("output.csv"));

var target = rec.parquetTarget(new File("results.parquet"), schema);
source.to(target);
```

### Usage (Java)

```java
ParquetSource source = new ParquetSource(Paths.get("data.parquet"));
DataFrame df = DataFrame.fromSource(source);

ParquetTarget target = new ParquetTarget(Paths.get("output.parquet"), schema);
source.to(target);
```

### Design

`ParquetSource` (280 lines) uses Hadoop's `ParquetReader` with a custom `GroupConverter` to map Parquet columns to Rec's `Schema` and `DataRow`.

`ParquetTarget` (277 lines) uses Hadoop's `ParquetWriter` with a custom `GroupWriteSupport` to serialize `DataSet` records.

### Dependencies

- `org.apache.parquet:parquet-hadoop`
- `org.apache.hadoop:hadoop-common`
