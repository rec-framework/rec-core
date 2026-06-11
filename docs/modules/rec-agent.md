# rec-agent — AI Agent System

The agent module integrates OpenAI's Chat Completions API with a comprehensive tool system, MCP client support, and an agentskills.io-compatible skill framework.

## Overview

The agent system provides an AI assistant that can:
- Execute file operations (read, write, edit, search)
- Run shell commands with safety checks
- Load, inspect, query, and export data using the Rec data model
- Invoke sub-agents for planning and codebase exploration
- Connect to external MCP servers for additional tools
- Load and activate skills following the agentskills.io spec

## Core Components

### Agent

The central class managing the conversation loop with OpenAI.

```java
// Using environment variables (OPENAI_API_KEY, OPENAI_BASE_URL)
Agent agent = new Agent("gpt-4o", systemPrompt, workspace);

// Using explicit endpoint configuration
Agent agent = new Agent("gpt-4o", systemPrompt, workspace,
    "http://localhost:11434/v1", "ollama");

String response = agent.chat("Load data.csv and summarize it");
agent.close();
```

**Constructors:**

- `Agent(String model, String systemPrompt, Path workspace)` — reads config from environment
- `Agent(String model, String systemPrompt, Path workspace, String baseUrl, String apiKey)` — explicit config

**Environment Variables:**

| Variable | Description |
|----------|-------------|
| `OPENAI_API_KEY` | API key for authentication |
| `OPENAI_BASE_URL` | Custom endpoint URL (for compatible APIs) |

**Initialization:**
- Creates `OpenAIOkHttpClient` from environment or explicit parameters
- Registers all built-in tools and sub-agents
- Loads workspace context (AGENTS.md + .agents/skills)
- Configurable workspace path for file tool sandboxing

**Conversation Loop** (max 50 iterations):
1. Build `ChatCompletionCreateParams` with tools + augmented system prompt
2. Call OpenAI API
3. If response contains tool calls → execute via `ToolRegistry` → add results → repeat
4. If no tool calls → return text response

### System Prompt Composition

The full system prompt is built from:
1. **AGENTS.md** content (if present at workspace root)
2. **User system prompt** (provided at construction)
3. **Active skill instructions** (from `SkillRegistry`)

## Tool System

### Tool Interface

All tools implement a common interface:

```java
public interface Tool {
    String name();                           // Function name for OpenAI
    String description();                    // Description for the model
    Map<String, Object> parametersSchema();  // JSON Schema for parameters
    String execute(Map<String, Object> args); // Execute and return result
}
```

### ToolRegistry

Manages tool registration, lookup, and conversion to OpenAI format:

```java
ToolRegistry registry = new ToolRegistry();
registry.register(new ReadTool(workspace));
registry.unregister("Bash");  // disable a tool

// Convert to OpenAI ChatCompletionTool list
List<ChatCompletionTool> tools = registry.toOpenAiTools();
```

### Built-in File Tools

| Tool | Parameters | Description |
|------|-----------|-------------|
| **Read** | `path`, `startLine?`, `endLine?` | Read file content with optional line range |
| **Write** | `path`, `content` | Create or overwrite a file |
| **Edit** | `path`, `oldText`, `newText` | Replace text in a file |
| **Glob** | `pattern`, `path?` | Find files matching a glob pattern |
| **Grep** | `pattern`, `path?`, `include?` | Regex search in files |
| **WebFetch** | `url`, `query?` | Fetch URL content via HTTP |
| **Bash** | `command`, `timeout?` | Execute shell command |

**Sandbox**: Write, Edit, and Bash are restricted to the workspace directory. Bash includes risk detection for dangerous patterns (`rm -rf`, `sudo`, `chmod`, etc.).

### Rec Data Tools

The agent has a single `ExecuteRecScript` tool for all Rec data operations. It executes JavaScript scripts using the Rec pipeline API via `require("rec")`.

| Tool | Parameters | Description |
|------|-----------|-------------|
| **ExecuteRecScript** | `script` or `path` | Execute a Rec JS script (inline code or file path) |

A built-in `rec-script` skill is automatically loaded, providing comprehensive documentation on the Rec API, pipeline patterns, and examples. The agent can activate this skill to learn how to write Rec scripts.

#### Example

```
rec> Load data.csv, filter rows where age > 30, and export to filtered.csv
[Tool: ExecuteRecScript]
Script executed successfully. Wrote 150 rows to filtered.csv.
```

### Sub-Agents

Sub-agents are prompt-driven tools that spawn child agent conversations with filtered tool subsets.

#### Plan Sub-Agent

- **Name**: `Plan`
- **Allowed Tools**: Read, Glob, Grep
- **Purpose**: Produce structured implementation plans for multi-step tasks

#### Explore Sub-Agent

- **Name**: `Explore`
- **Allowed Tools**: Read, Glob, Grep, Bash
- **Purpose**: Investigate and understand codebases

Both sub-agents run their own conversation loop (max 20 iterations) with an independent context.

## MCP Client Bridge

Connect to external MCP (Model Context Protocol) servers to extend the agent's tool set.

```java
agent.addMcpServer("npx", "-y", "@modelcontextprotocol/server-filesystem", "/path");
```

### How It Works

1. `McpToolBridge.stdio()` creates a `StdioClientTransport` with `ServerParameters`
2. Initializes an `McpSyncClient` and calls `listTools()`
3. Each MCP tool is wrapped in a `McpToolAdapter` implementing the `Tool` interface
4. Adapter's `execute()` calls `client.callTool(CallToolRequest)` and formats the result

### Supported Transport

Currently supports **stdio transport** only. The MCP server is launched as a subprocess and communicates via stdin/stdout.

## Skills System

### Skill Format

Skills follow the [agentskills.io](https://agentskills.io/) specification:

```markdown
---
name: data-analysis
description: Analyze CSV data and produce summary statistics
---

## Instructions
When asked to analyze data:
1. Use the `ExecuteRecScript` tool to write a Rec script that loads the CSV
2. Filter and aggregate within the script
3. Present findings as a markdown table
```

### Progressive Disclosure

Skills are loaded in two stages:
1. **Discovery** — Only YAML frontmatter (name, description) is parsed. Skills appear in the system prompt as a list.
2. **Activation** — Full markdown body is loaded on demand via `SkillRegistry.activate()`.

### SkillRegistry

```java
SkillRegistry registry = new SkillRegistry();
registry.addDirectories(Paths.get(".agents/skills"));

// List discovered skills
List<Skill> skills = registry.listDiscovered();

// Activate a skill (loads full body)
String instructions = registry.activate("data-analysis");

// Build context for system prompt
String context = registry.buildSkillsContext();
```

### Auto-Discovery

Skills in `.agents/skills/` are auto-discovered when the agent is created:

```
workspace/
├── .agents/
│   └── skills/
│       ├── data-analysis/
│       │   └── SKILL.md
│       └── code-review/
│           └── SKILL.md
```

## Workspace Context

### AgentsMdLoader

Reads `AGENTS.md` from the workspace root and injects its content into the system prompt:

```java
String content = AgentsMdLoader.load(workspace);  // "" if not found
```

### WorkspaceContext

Combines AGENTS.md loading and skill auto-discovery:

```java
WorkspaceContext ctx = WorkspaceContext.load(workspace);
ctx.registerSkills(skillRegistry);  // auto-discover .agents/skills/
```

## Agent TUI

Interactive terminal UI for chatting with the agent. Built with JLine3.

### Usage

```bash
./rec --agent                                          # Default: gpt-4o, current dir
./rec --agent --model gpt-4o-mini                      # Different model
./rec --agent --workspace /my/project                  # Set workspace
./rec --agent --system-prompt "Be terse"               # Custom system prompt
./rec --agent --api-key sk-...                         # API key (alternative to env var)
./rec --agent --base-url http://localhost:11434/v1     # Custom endpoint
```

### Custom Endpoints

The agent supports any OpenAI-compatible API:

```bash
# Ollama
./rec --agent --base-url http://localhost:11434/v1 --api-key ollama --model llama3

# Azure OpenAI (via environment variables)
export OPENAI_BASE_URL=https://your-resource.openai.azure.com
export OPENAI_API_KEY=your-azure-key
./rec --agent

# vLLM / LiteLLM
./rec --agent --base-url http://localhost:8000/v1 --api-key dummy --model meta-llama/Llama-3
```

| Environment Variable | CLI Flag | Description |
|---------------------|----------|-------------|
| `OPENAI_API_KEY` | `--api-key` | API key |
| `OPENAI_BASE_URL` | `--base-url` | Custom endpoint URL |
| — | `--model` | Model name (default: `gpt-4o`) |
| — | `--workspace` | Working directory for file tools |
| — | `--system-prompt` | Custom system prompt |

### Features

- **Readline-like input** with history (JLine3)
- **Multi-line input** — lines ending with `\` continue on next line
- **Color output** — ANSI escape codes for responses and tool calls
- **Inline tool call display** — `[Tool: Read /path/to/file]`

### Commands

| Command | Description |
|---------|-------------|
| `/quit`, `/exit` | Exit the TUI |
| `/tools` | List all registered tools with descriptions |
| `/skills` | List discovered skills and activation status |
| `/clear` | Reset conversation (visual only) |
| `/help` | Show command help |

### Graceful Shutdown

Ctrl+C or `/quit` exits cleanly, closing MCP connections.

## JS Integration

The agent is exposed to JS scripts via the `AgentModule` plugin:

```javascript
var rec = require("rec");

// Create an agent
var agent = rec.createAgent("gpt-4o");

// Chat with the agent
var response = rec.chat(agent, "List files in the current directory");
print(response);

// Add a custom tool
rec.addTool(agent, "greet", "Say hello", {
  type: "object",
  properties: { name: { type: "string" } },
  required: ["name"]
}, function(args) {
  return "Hello, " + args.name + "!";
});

// Add MCP server
rec.addMcpServer(agent, "npx", "-y", "my-mcp-server");

// Manage skills
rec.addSkillDirectories(agent, "/path/to/skills");
rec.activateSkill(agent, "data-analysis");

// List tools and skills
print(rec.listTools(agent));
print(rec.listSkills(agent));

// Clean up
rec.close(agent);
```

### AgentModule API

| Method | Description |
|--------|-------------|
| `createAgent(model)` | Create agent with default system prompt |
| `createAgent(model, systemPrompt)` | Create agent with custom prompt |
| `createAgent(model, systemPrompt, workspace)` | Create agent with workspace |
| `chat(agent, message)` | Send message and get response |
| `addTool(agent, name, desc, schema, fn)` | Register a custom tool |
| `addMcpServer(agent, command, args...)` | Connect an MCP server |
| `addSkillDirectories(agent, dirs...)` | Add skill directories |
| `activateSkill(agent, name)` | Activate a skill |
| `removeBuiltinTool(agent, name)` | Disable a built-in tool |
| `listTools(agent)` | List tool names |
| `listSkills(agent)` | List discovered skills |
| `close(agent)` | Close agent and MCP connections |

## Design Decisions

### Circular Dependency Avoidance

`rec-agent` depends on `rec-core` only. The `ExecuteRecScript` tool uses reflection to call `Scripting.runfile()` from `rec-scripting`, avoiding a compile-time dependency cycle.

### OpenAI SDK Usage

Uses the official `com.openai:openai-java:4.39.1` SDK with low-level `ChatCompletionTool` + `FunctionDefinition` builders. Tool parameter schemas are `Map<String, Object>` converted to `FunctionParameters` via `JsonValue.from()`.

The client supports custom endpoints via `OPENAI_BASE_URL` environment variable or the `--base-url` CLI flag, enabling compatibility with Azure OpenAI, Ollama, vLLM, LiteLLM, and other OpenAI-compatible services.

### MCP SDK Usage

Uses `io.modelcontextprotocol.sdk:mcp:1.0.2` with `McpSyncClient` and `StdioClientTransport`. The Jackson 3.x (`tools.jackson.databind`) used by the MCP SDK coexists with Jackson 2.x (`com.fasterxml.jackson`) used by the OpenAI SDK since they use different packages.
