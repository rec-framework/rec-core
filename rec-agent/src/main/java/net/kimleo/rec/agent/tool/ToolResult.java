package net.kimleo.rec.agent.tool;

/**
 * Wraps the result of executing a tool.
 */
public record ToolResult(String toolCallId, String content, boolean isError) {

    public static ToolResult success(String toolCallId, String content) {
        return new ToolResult(toolCallId, content, false);
    }

    public static ToolResult error(String toolCallId, String error) {
        return new ToolResult(toolCallId, error, true);
    }
}
