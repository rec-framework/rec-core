# Rec — Data Pipeline Framework

**Version**: 0.2.2 | **Java**: 17 | **Build**: Gradle

Rec is a data pipeline framework with a JavaScript scripting layer and an AI agent system. It provides a streaming **Source → Tee → Target** pipeline model where data flows from sources through transformation and inspection stages to targets.

## Features

- **Streaming Pipeline**: Functional data pipeline with `Source`, `Tee`, and `Target` abstractions
- **JavaScript Scripting**: Rhino-based JS engine with a rich API for building data pipelines
- **AI Agent System**: OpenAI-powered agent with tool calling, MCP support, and skills
- **Plugin Architecture**: Extensible via `ServiceLoader`-based plugin discovery
- **Multiple Formats**: CSV, JSONL, Parquet, and binary file support
- **Database Integration**: JDBI3-based database source support

## Modules

| Module | Description |
|--------|-------------|
| [rec-core](modules/rec-core.md) | Foundation — data model, pipeline interfaces, CSV parsing |
| [rec-scripting](modules/rec-scripting.md) | Rhino JS engine, CLI entry point, fat JAR |
| [rec-agent](modules/rec-agent.md) | AI agent with OpenAI, tool calling, MCP, and skills |
| [rec-cache](modules/plugins.md#rec-cache) | Binary and in-memory caching tees |
| [rec-jdbi](modules/plugins.md#rec-jdbi) | JDBI3 database source |
| [rec-reactive](modules/plugins.md#rec-reactive) | Push-based reactive pipeline |
| [rec-datatype-jsonl](modules/plugins.md#rec-datatype-jsonl) | JSON Lines reader/writer |
| [rec-datatype-parquet](modules/plugins.md#rec-datatype-parquet) | Apache Parquet reader/writer |

## Quick Start

### Build

```bash
./gradlew :rec-scripting:fatJar
```

### Run a Script

```bash
./rec script my-pipeline.js
```

### Start the Agent TUI

```bash
./rec --agent
./rec --agent --model gpt-4o-mini
./rec --agent --workspace /path/to/project
./rec --agent --base-url http://localhost:11434/v1 --api-key ollama --model llama3  # Custom endpoint
```

## Documentation

- [Architecture](architecture.md) — System design and module interactions
- [Getting Started](getting-started.md) — Tutorials and examples
- [Module Documentation](modules/) — Detailed docs for each module
- [Extending Rec](extending.md) — How to create plugins, tools, and skills
