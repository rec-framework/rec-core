# Getting Started

## Prerequisites

- Java 17 or later
- Gradle (included via wrapper: `./gradlew`)
- For the agent: `OPENAI_API_KEY` environment variable

## Installation

### Build from Source

```bash
git clone <repo-url> rec-core
cd rec-core
./gradlew :rec-scripting:fatJar
```

This produces a single executable JAR at `rec-scripting/build/libs/rec-core-*-all.jar`.

### Using the `./rec` Script

The `./rec` shell script wraps the fat JAR:

```bash
chmod +x rec
./rec script my-pipeline.js
```

If the JAR doesn't exist, `./rec` will auto-build it. Use `--build` to force a rebuild:

```bash
./rec --build script my-pipeline.js
```

## Your First Pipeline

Create a file `hello.js`:

```javascript
var rec = require("rec");

// Create a CSV source from a file
var source = rec.csv(rec.file("data.csv"), ",", "name,age,city");

// Build a pipeline: filter → transform → output
source
  .filter(rec.pred(function(row) {
    return row.getInt(1) > 25;  // age > 25
  }))
  .tee(rec.action(function(row) {
    print("Processing: " + row.getString(0));
  }))
  .to(rec.flat("output.csv"));
```

Run it:

```bash
./rec script hello.js
```

## Pipeline Concepts

### Source → Tee → Target

```
CSV File → [Filter] → [Transform] → [Cache] → Output File
```

- **Source**: Where data comes from (CSV, JSONL, Parquet, database)
- **Tee**: Processing stage — filters, transforms, counts, caches
- **Target**: Where data goes (file, custom function, collector)

### Common Patterns

#### Read CSV and Write Filtered Output

```javascript
var rec = require("rec");

rec.csv(rec.file("input.csv"), ",", "id,name,value")
  .filter(rec.pred(function(row) {
    return row.getDouble(2) > 100;
  }))
  .to(rec.flat("filtered.csv"));
```

#### Count Records

```javascript
var counter = rec.counter(function(row) {
  return row.getString(2) === "active";
});

source.tee(counter).to(rec.dummy());
print("Active records: " + counter.count());
```

#### Cache Intermediate Results

```javascript
source
  .tee(rec.cache(4096))      // binary cache, 4KB buffer
  .tee(expensiveTransform)
  .to(rec.flat("output.csv"));
```

#### Collect into Memory

```javascript
var collector = rec.collect();
source.tee(collector).to(rec.dummy());

var records = collector.list();  // access all collected records
```

## Using the Agent

### Interactive TUI

```bash
export OPENAI_API_KEY=sk-...
./rec --agent
```

The agent TUI provides a REPL where you can chat with an AI assistant that has access to file tools, data tools, and sub-agents.

```
rec> Load the file data.csv and show me the first 5 rows
[Tool: ExecuteRecScript]
Loaded 'data' with 1000 rows...
```

### TUI Commands

| Command | Description |
|---------|-------------|
| `/tools` | List all available tools |
| `/skills` | List discovered skills |
| `/clear` | Reset conversation history |
| `/help` | Show help |
| `/quit` | Exit the TUI |

### CLI Options

```bash
./rec --agent --model gpt-4o-mini           # Use a different model
./rec --agent --workspace /my/project       # Set workspace directory
./rec --agent --system-prompt "You are..."  # Custom system prompt
./rec --agent --api-key sk-...              # API key (alternative to env var)
./rec --agent --base-url http://host/v1     # Custom endpoint (see below)
```

### Using Custom Endpoints

The agent supports any OpenAI-compatible API endpoint:

```bash
# Ollama (local models)
./rec --agent --base-url http://localhost:11434/v1 --api-key ollama --model llama3

# Azure OpenAI
export OPENAI_BASE_URL=https://your-resource.openai.azure.com
export OPENAI_API_KEY=your-azure-key
./rec --agent --model gpt-4o

# vLLM / LiteLLM / other compatible servers
./rec --agent --base-url http://localhost:8000/v1 --api-key dummy --model meta-llama/Llama-3
```

Configuration can be set via environment variables or CLI flags:

| Environment Variable | CLI Flag | Description |
|---------------------|----------|-------------|
| `OPENAI_API_KEY` | `--api-key` | API key |
| `OPENAI_BASE_URL` | `--base-url` | Custom endpoint URL |
| — | `--model` | Model name (default: `gpt-4o`) |
| — | `--workspace` | Working directory for file tools |
| — | `--system-prompt` | Custom system prompt |

### Agent in JS Scripts

```javascript
var rec = require("rec");
var agent = rec.createAgent("gpt-4o");
var response = rec.chat(agent, "Analyze data.csv and summarize findings");
print(response);
rec.close(agent);
```

## Workspace Context

The agent automatically loads context from the working directory:

```
my-project/
├── AGENTS.md              # Project instructions (injected into system prompt)
├── .agents/
│   └── skills/            # Auto-discovered skills
│       ├── analysis/
│       │   └── SKILL.md
│       └── review/
│           └── SKILL.md
└── data.csv
```

### AGENTS.md

If present at the workspace root, its content is prepended to the agent's system prompt:

```markdown
# Project: My Data Pipeline

## Conventions
- All CSV files use comma delimiter
- Column names are lowercase with underscores
- Output files go to ./output/
```

### Skills

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
