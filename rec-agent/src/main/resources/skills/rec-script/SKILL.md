---
name: rec-script
description: Write and execute Rec data pipeline scripts in JavaScript
---

# Rec Data Pipeline Scripting

You can write and execute Rec data pipeline scripts using the `ExecuteRecScript` tool.
Scripts are written in JavaScript and have full access to the Rec API via `require("rec")`.

## How to Use

Use the `ExecuteRecScript` tool to run scripts. You can provide:
- `script`: Inline JavaScript code
- `path`: Path to a `.js` file

### Basic Template

```javascript
var rec = require("rec");

// Load data
var source = rec.csv(rec.file("data.csv"), ",", "name,age,city");

// Process with pipeline
source.stream()
    .filter(rec.pred(function(row) { return row.get("age") > 18; }))
    .map(rec.action(function(row) { rec.println(row.get("name")); }))
    .forEach(rec.action(function(row) {}));
```

## Pipeline Architecture

Rec uses a **source → stream → pipeline** model:

1. **Source**: Loads data from files, databases, or collections
2. **Stream**: Converts to a Java Stream for processing
3. **Pipeline**: Chain of `filter`, `map`, `forEach` operations

### Key Pattern

```javascript
source.stream()
    .filter(rec.pred(fn))      // Filter rows (Predicate)
    .map(rec.action(fn))       // Transform rows (Consumer)
    .forEach(rec.action(fn));  // Terminal operation (Consumer)
```

## API Reference

### Source Creation

| Function | Description | Example |
|----------|-------------|---------|
| `rec.csv(reader, delimiter, accessors)` | CSV file source | `rec.csv(rec.file("data.csv"), ",", "name,age")` |
| `rec.query(url, user, password, sql)` | JDBC database query | `rec.query("jdbc:h2:db", "sa", "", "SELECT *")` |
| `rec.stream(stream)` | From Java Stream | `rec.stream(myStream)` |
| `rec.jsonl(source)` | JSONL file source | `rec.jsonl(rec.file("data.jsonl"))` |
| `rec.parquet(file)` | Parquet file source | `rec.parquet(new File("data.parquet"))` |

### Predicate & Action Wrappers

JavaScript functions must be wrapped for Java interop:

```javascript
rec.pred(function(row) { ... })      // Wrap as Predicate<Object>
rec.action(function(row) { ... })    // Wrap as Consumer<Object>
```

### Row Access

Rows are Java Maps. Use these methods:

```javascript
row.get("column")          // Get value
row.put("column", value)   // Set value
row.containsKey("column")  // Check existence
```

### Output & Collection

| Function | Description | Example |
|----------|-------------|---------|
| `rec.println(...)` | Print to stdout | `rec.println("Result:", row.get("name"))` |
| `rec.collect()` | Collect stream to list | `source.stream().collect(rec.collect())` |
| `rec.counter(pred)` | Count matching rows | `source.stream().collect(rec.counter(rec.pred(...)))` |
| `rec.unique(...keys)` | Deduplicate by keys | `source.stream().collect(rec.unique("id", "name"))` |
| `rec.stateful(initial, fn)` | Stateful aggregation | `source.stream().collect(rec.stateful({...}, fn))` |

### Targets (Write Output)

| Function | Description | Example |
|----------|-------------|---------|
| `rec.flat(filename)` | Write to flat file | `source.stream().forEach(rec.flat("out.txt"))` |
| `rec.jsonlTarget(file)` | Write JSONL file | `source.stream().forEach(rec.jsonlTarget(new File("out.jsonl")))` |
| `rec.parquetTarget(file)` | Write Parquet file | `source.stream().forEach(rec.parquetTarget(new File("out.parquet")))` |
| `rec.target(fn)` | Custom target function | `source.stream().forEach(rec.target(function(row) {...}))` |
| `rec.dummy()` | Discard output | `source.stream().forEach(rec.dummy())` |

### Caching & Restartability

| Function | Description | Example |
|----------|-------------|---------|
| `rec.cache(size)` | Buffered caching tee | `source.stream().collect(rec.cache(1000))` |
| `rec.inMemoryCache(size)` | In-memory cache | `source.stream().collect(rec.inMemoryCache(100))` |
| `rec.restartable(source)` | Restartable source | `rec.restartable(source).stream()...` |

## Complete Examples

### Load CSV, filter, and print

```javascript
var rec = require("rec");
var source = rec.csv(rec.file("users.csv"), ",", "name,age,email");
source.stream()
    .filter(rec.pred(function(row) { return row.get("age") >= 21; }))
    .forEach(rec.action(function(row) {
        rec.println(row.get("name") + " - " + row.get("email"));
    }));
```

### Load from database and collect

```javascript
var rec = require("rec");
var source = rec.query("jdbc:postgresql://localhost/db", "user", "pass", 
    "SELECT id, name FROM customers WHERE active = true");
var results = source.stream().collect(rec.collect());
results.forEach(rec.action(function(row) { rec.println(row); }));
```

### Load JSONL and transform

```javascript
var rec = require("rec");
var source = rec.jsonl(rec.file("logs.jsonl"));
source.stream()
    .map(rec.action(function(row) {
        row.put("processed", true);
        row.put("timestamp", new Date().getTime());
    }))
    .forEach(rec.jsonlTarget(new File("processed.jsonl")));
```

### Stateful aggregation

```javascript
var rec = require("rec");
var source = rec.csv(rec.file("sales.csv"), ",", "product,amount");
source.stream()
    .collect(rec.stateful({total: 0}, function(row, state) {
        state.total += Number(row.get("amount"));
        return state;
    }));
```

### Count and deduplicate

```javascript
var rec = require("rec");
var source = rec.csv(rec.file("data.csv"), ",", "id,name,value");
var count = source.stream().collect(rec.counter(rec.pred(function(row) {
    return row.get("value") > 100;
})));
rec.println("High-value rows:", count);

var unique = source.stream().collect(rec.unique("id"));
```

## Tips

1. **Always use `rec.pred()` and `rec.action()`** to wrap JS functions
2. **Use `rec.file(path)`** for file readers (resolves relative to workspace)
3. **Use `new File(path)`** for Java File objects (targets like jsonlTarget, parquetTarget)
4. **Terminal operations** are required: `forEach`, `collect`, or `count`
5. **Chain operations** fluently: `filter().map().filter().forEach()`
6. **Print for debugging**: `rec.println("Debug:", row.get("field"))`

## Error Handling

If the script fails, the tool returns the error message. Common issues:
- Missing columns: Check accessor names match CSV headers
- Type errors: Use `Number()`, `String()` for type conversion
- Null values: Check `row.get("field") != null` before use
