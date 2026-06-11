# Extending Rec

Rec is designed for extensibility. This guide covers how to create plugins, custom tools, sub-agents, skills, and new data formats.

---

## Creating a Plugin

Plugins are discovered via Java's `ServiceLoader` mechanism.

### Step 1: Implement RecPlugin

```java
package net.kimleo.rec.plugin;

public class MyPlugin implements RecPlugin {
    @Override
    public String name() {
        return "myplugin";  // accessible as __myplugin in JS
    }

    @Override
    public Object module() {
        return this;  // public methods become JS functions
    }

    // Methods exposed to JS
    public Source readFile(String path) {
        return new CSVFileSource(path, ',', Schema.of("a", "b", "c"));
    }

    public Target writeTo(String path) {
        return new FlatFileTarget(path);
    }
}
```

### Step 2: Register via ServiceLoader

Create the file `src/main/resources/META-INF/services/net.kimleo.rec.plugin.RecPlugin`:

```
net.kimleo.rec.plugin.MyPlugin
```

### Step 3: Add as Dependency

In `rec-scripting/build.gradle`, add your plugin module:

```gradle
dependencies {
    runtimeOnly project(':rec-myplugin')
}
```

### Step 4: Use in JS

```javascript
var result = __myplugin.readFile("input.csv");
result.to(__myplugin.writeTo("output.csv"));
```

### Design Guidelines

- **Keep the module interface small** — expose high-level operations, not internal details
- **Return pipeline types** — return `Source`, `Tee`, or `Target` when possible
- **Handle errors gracefully** — wrap checked exceptions in `RecException`
- **Thread safety** — the module object may be called from multiple threads in future versions

---

## Creating a Custom Tool

Tools extend the agent's capabilities.

### Step 1: Implement the Tool Interface

```java
import net.kimleo.rec.agent.tool.Tool;
import java.util.Map;

public class WeatherTool implements Tool {
    @Override
    public String name() {
        return "GetWeather";
    }

    @Override
    public String description() {
        return "Get current weather for a location";
    }

    @Override
    public Map<String, Object> parametersSchema() {
        return Map.of(
            "type", "object",
            "properties", Map.of(
                "location", Map.of(
                    "type", "string",
                    "description", "City name or ZIP code"
                ),
                "units", Map.of(
                    "type", "string",
                    "enum", List.of("celsius", "fahrenheit"),
                    "description", "Temperature units"
                )
            ),
            "required", List.of("location")
        );
    }

    @Override
    public String execute(Map<String, Object> args) {
        String location = (String) args.get("location");
        // Call weather API...
        return "Current weather in " + location + ": 22°C, sunny";
    }
}
```

### Step 2: Register the Tool

**Via Agent (Java):**

```java
Agent agent = new Agent("gpt-4o");
agent.registry().register(new WeatherTool());
```

**Via AgentModule (JS):**

```javascript
var rec = require("rec");
var agent = rec.createAgent("gpt-4o");
rec.addTool(agent, "GetWeather", "Get weather for a location", {
    type: "object",
    properties: {
        location: { type: "string", description: "City name" }
    },
    required: ["location"]
}, function(args) {
    return "Weather in " + args.location + ": 22°C, sunny";
});
```

### Tool Best Practices

- **Clear descriptions** — the model uses descriptions to decide when to call your tool
- **JSON Schema parameters** — include types, descriptions, enums, and required fields
- **Return structured text** — return results as formatted strings (tables, lists)
- **Handle errors** — return error messages as strings, not exceptions
- **Be stateless** — tools should be safe to call multiple times

---

## Creating a Sub-Agent

Sub-agents are tools that spawn child conversations with their own context.

### Step 1: Extend SubAgent

```java
import net.kimleo.rec.agent.subagent.SubAgent;
import java.util.Set;

public class DataAnalysisAgent extends SubAgent {

    public DataAnalysisAgent(Agent parent, ToolRegistry registry) {
        super(parent, registry);
    }

    @Override
    public String name() {
        return "AnalyzeData";
    }

    @Override
    public String description() {
        return "Analyze data files and produce statistical summaries";
    }

    @Override
    protected String systemPrompt() {
        return "You are a data analyst. Use the provided tools to "
             + "examine data files, compute statistics, and present "
             + "findings in a clear format.";
    }

    @Override
    protected Set<String> allowedTools() {
        return Set.of("Read", "Glob", "Grep", "Bash",
                       "ExecuteRecScript");
    }
}
```

### Step 2: Register

```java
agent.registry().register(
    new DataAnalysisAgent(agent, agent.registry())
);
```

### Sub-Agent Design

- **Limited tool set** — restrict which tools the sub-agent can use via `allowedTools()`
- **Focused system prompt** — give the sub-agent a clear, specific role
- **Independent context** — sub-agents run their own conversation loop (max 20 iterations)
- **Return summary** — the sub-agent's final response is returned to the parent agent

---

## Creating Skills

Skills are markdown files that teach the agent how to perform specific tasks.

### File Structure

```
.agents/skills/my-skill/
└── SKILL.md
```

### SKILL.md Format

```markdown
---
name: my-skill
description: Brief description shown in the skills list
---

## Instructions

Detailed markdown instructions that the model follows when this skill is activated.

### Step 1
Write a Rec script to load the data file.

### Step 2
Inspect the output to understand the schema.

### Step 3
Apply the transformation and export results.

## Examples

### Example 1
User: "Analyze sales.csv"
Agent: Loads the file, inspects 5 rows, filters Q4 data, exports to q4_sales.csv
```

### YAML Frontmatter Fields

| Field | Required | Description |
|-------|----------|-------------|
| `name` | Yes | Unique skill identifier |
| `description` | Yes | Short description for the skills list |

### Progressive Disclosure

Skills use lazy loading:
1. **Discovery** — Only `name` and `description` are parsed from frontmatter
2. **Activation** — Full markdown body loaded when the skill is activated
3. **Injection** — Active skill instructions are appended to the system prompt

### Skill Auto-Discovery

Skills in `.agents/skills/` are automatically discovered when the agent starts:

```
workspace/
├── AGENTS.md
└── .agents/
    └── skills/
        ├── analysis/SKILL.md      # auto-discovered
        └── reporting/SKILL.md     # auto-discovered
```

### Manual Skill Loading

```javascript
var rec = require("rec");
var agent = rec.createAgent("gpt-4o");
rec.addSkillDirectories(agent, "/custom/path/to/skills");
rec.activateSkill(agent, "my-skill");
```

---

## Creating a New Data Format

### Step 1: Implement Source

```java
import net.kimleo.rec.model.Source;
import net.kimleo.rec.data.DataSet;
import java.util.stream.Stream;

public class XmlSource implements Source {
    private final Path file;
    private final Schema schema;

    @Override
    public Stream<DataSet> stream() {
        // Parse XML and return a stream of DataSet records
        return parseXml(file).map(row -> new XmlDataRow(row, schema));
    }
}
```

### Step 2: Implement Target (Optional)

```java
import net.kimleo.rec.model.Target;

public class XmlTarget implements Target {
    @Override
    public void put(DataSet dataSet) {
        // Write DataSet as XML
    }
}
```

### Step 3: Create Plugin

```java
public class XmlPlugin implements RecPlugin {
    @Override
    public String name() { return "xml"; }

    @Override
    public Object module() { return this; }

    public Source file(String path) {
        return new XmlSource(Paths.get(path));
    }

    public Target output(String path) {
        return new XmlTarget(Paths.get(path));
    }
}
```

### Step 4: Register and Use

```javascript
var source = __xml.file("data.xml");
source.to(rec.flat("output.csv"));
```

---

## MCP Server Integration

### Connecting External Tools

The agent can connect to any MCP-compatible server:

```javascript
var rec = require("rec");
var agent = rec.createAgent("gpt-4o");

// Filesystem MCP server
rec.addMcpServer(agent, "npx", "-y",
    "@modelcontextprotocol/server-filesystem", "/data");

// Custom MCP server
rec.addMcpServer(agent, "node", "my-mcp-server.js");
```

### MCP Tool Discovery

When an MCP server is connected:
1. `listTools()` is called to discover available tools
2. Each tool is adapted to the `Tool` interface via `McpToolAdapter`
3. Tool schemas are extracted from MCP's `inputSchema`
4. Tools are registered in the agent's `ToolRegistry`

### MCP Transport

Currently supports **stdio transport**:
- Server is launched as a subprocess
- Communication via stdin/stdout
- Uses JSON-RPC protocol
- Requires `McpJsonMapper` (Jackson 3.x based)

---

## Testing Extensions

### Plugin Tests

```java
@Test
public void testPluginName() {
    MyPlugin plugin = new MyPlugin();
    assertEquals("myplugin", plugin.name());
    assertNotNull(plugin.module());
}
```

### Tool Tests

```java
@Test
public void testToolExecution() {
    WeatherTool tool = new WeatherTool();
    assertEquals("GetWeather", tool.name());

    Map<String, Object> args = Map.of("location", "NYC");
    String result = tool.execute(args);
    assertTrue(result.contains("NYC"));
}
```

### Skill Tests

```java
@Test
public void testSkillParsing() {
    Skill skill = SkillLoader.load(skillDir);
    assertEquals("my-skill", skill.name());
    assertFalse(skill.description().isEmpty());
}
```
