package net.kimleo.rec.agent.tool;

import java.util.Map;

/**
 * Value object representing a tool call extracted from the model response.
 */
public record ToolCall(String id, String name, Map<String, Object> arguments) {
}
