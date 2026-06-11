# API Reference

## JavaScript API

### Core Module (`require("rec")`)

The main API surface for building data pipelines in JavaScript.

---

#### `rec.file(path)`

Open a file for reading.

```javascript
var reader = rec.file("data.csv");
```

**Parameters:**
- `path` (String) — File path

**Returns:** `java.io.Reader`

---

#### `rec.csv(reader, delimiter, accessor)`

Create a CSV source from a reader.

```javascript
var source = rec.csv(rec.file("data.csv"), ",", "name,age,city");
```

**Parameters:**
- `reader` — A `java.io.Reader`
- `delimiter` (String) — Column delimiter (e.g., `","`, `"\t"`, `"|"`)
- `accessor` (String) — Comma-separated column names

**Returns:** `Source`

---

#### `rec.action(fn)`

Create a Tee that applies a transformation function.

```javascript
var upper = rec.action(function(row) {
  var name = row.getString(0).toUpperCase();
  // return modified row or original
  return row;
});
```

**Parameters:**
- `fn` (Function) — `(DataSet) => DataSet`

**Returns:** `Tee`

---

#### `rec.pred(fn)`

Create a predicate for filtering.

```javascript
var isAdult = rec.pred(function(row) {
  return row.getInt(1) >= 18;
});
```

**Parameters:**
- `fn` (Function) — `(DataSet) => boolean`

**Returns:** `Predicate<DataSet>`

---

#### `rec.stateful(init, fn)`

Create a stateful Tee with an accumulator.

```javascript
var sum = rec.stateful(
  function() { return { total: 0 }; },
  function(state, row) {
    state.total += row.getDouble(2);
    return row;
  }
);
```

**Parameters:**
- `init` (Function) — `() => State` — Initial state factory
- `fn` (Function) — `(State, DataSet) => DataSet` — Transform with state

**Returns:** `Tee`

---

#### `rec.flat(path)`

Create a CSV file target.

```javascript
source.to(rec.flat("output.csv"));
```

**Parameters:**
- `path` (String) — Output file path

**Returns:** `Target`

---

#### `rec.target(fn)`

Create a custom target from a function.

```javascript
source.to(rec.target(function(row) {
  print(row.getString(0) + ": " + row.getInt(1));
}));
```

**Parameters:**
- `fn` (Function) — `(DataSet) => void`

**Returns:** `Target`

---

#### `rec.dummy()`

Discard all records.

```javascript
source.to(rec.dummy());
```

**Returns:** `Target`

---

#### `rec.counter(fn)`

Count records matching a predicate.

```javascript
var counter = rec.counter(function(row) {
  return row.getString(2) === "active";
});
source.tee(counter).to(rec.dummy());
print("Active: " + counter.count());
```

**Parameters:**
- `fn` (Function) — `(DataSet) => boolean`

**Returns:** `ItemCounterTee` (has `.count()` method)

---

#### `rec.collect()`

Collect all records into a list.

```javascript
var collector = rec.collect();
source.tee(collector).to(rec.dummy());
var records = collector.list();
```

**Returns:** `CollectTee` (has `.list()` method)

---

#### `rec.cache(bufferSize)`

Create a binary caching tee.

```javascript
source.tee(rec.cache(4096)).tee(expensiveOp).to(target);
```

**Parameters:**
- `bufferSize` (int) — Buffer size in bytes

**Returns:** `BufferedCachingTee`

---

#### `rec.println(msg)`

Print to stdout.

```javascript
rec.println("Processing complete");
```

---

### Source API

Methods available on all `Source` objects:

#### `source.tee(tee)`

Chain a Tee into the pipeline.

```javascript
source.tee(transform).tee(cache).tee(counter);
```

**Returns:** `Source` (chainable)

---

#### `source.filter(predicate)`

Filter records.

```javascript
source.filter(rec.pred(function(row) {
  return row.getInt(1) > 0;
}));
```

**Returns:** `Source`

---

#### `source.skip(n)`

Skip first N records.

```javascript
source.skip(10);
```

**Returns:** `Source`

---

#### `source.to(target)`

Consume all records into a Target.

```javascript
source.to(rec.flat("output.csv"));
source.to(rec.target(function(row) { /* ... */ }));
```

---

### DataSet API

Methods available on `DataSet` (row) objects:

```javascript
row.schema()           // Schema
row.isNull(0)          // boolean — is null?
row.isNull("name")     // boolean — by column name

row.getString(0)       // String
row.getString("name")  // by column name

row.getInt(0)          // int
row.getInt("age")      // by column name

row.getLong(0)         // long
row.getDouble(0)       // double
row.getFloat(0)        // float
row.getBoolean(0)      // boolean
```

---

## Java API

### Data Model

#### Schema

```java
Schema schema = Schema.of("name", "age", "city");
Schema typed = Schema.typed(names, types);

int count = schema.columnCount();
int idx = schema.index("name");
String col = schema.name(0);
DataType type = schema.type(0);
```

#### DataFrame

```java
DataFrame df = DataFrame.builder(schema)
    .addRow("Alice", "30")
    .addRow("Bob", "25")
    .build();

int rows = df.rowCount();
DataRow row = df.row(0);
Stream<DataSet> allRows = df.rows();
```

#### DataType

```java
DataType t = DataType.infer("123");      // INT
DataType t2 = DataType.infer("3.14");    // DOUBLE
Object def = DataType.STRING.defaultValue();  // ""
```

### Pipeline

#### Source

```java
Source source = new CSVFileSource("data.csv", ',', Schema.of("a", "b"));
source.stream().forEach(row -> System.out.println(row.getString(0)));
source.tee(tee).filter(pred).to(target);
```

#### Tee

```java
Tee tee = new TransformTee(row -> /* transform */ row);
Tee counter = new ItemCounterTee(row -> row.getInt(1) > 0);
Tee collector = new CollectTee();
```

#### Target

```java
Target target = new FlatFileTarget("output.csv");
target.put(row);
target.putAll(source);
```

### Agent

```java
Agent agent = new Agent("gpt-4o", "You are a helpful assistant.", workspace);
String response = agent.chat("Hello");
String toolCalls = agent.lastToolCalls();
ToolRegistry registry = agent.registry();
agent.close();
```

### Tool

```java
public interface Tool {
    String name();
    String description();
    Map<String, Object> parametersSchema();
    String execute(Map<String, Object> args);
}
```

### ToolRegistry

```java
ToolRegistry registry = new ToolRegistry();
registry.register(tool);
registry.unregister("ToolName");
Tool tool = registry.lookup("Read");
List<Tool> all = registry.list();
List<ChatCompletionTool> openAiTools = registry.toOpenAiTools();
```

### McpToolBridge

```java
McpToolBridge bridge = McpToolBridge.stdio("npx", "-y", "mcp-server", "/path");
List<Tool> tools = bridge.discoverTools();
String result = bridge.callTool("toolName", Map.of("arg", "value"));
bridge.close();
```

### Skill

```java
Skill skill = SkillLoader.load(skillDir);
String name = skill.name();
String desc = skill.description();
String body = skill.body();  // lazy load
```

### SkillRegistry

```java
SkillRegistry registry = new SkillRegistry();
registry.addDirectories(Paths.get(".agents/skills"));
List<Skill> discovered = registry.listDiscovered();
String instructions = registry.activate("skill-name");
String context = registry.buildSkillsContext();
```
