package net.kimleo.rec.agent;

/**
 * Callback interface for receiving streaming output from the agent.
 * The TUI implements this to display content, reasoning, and tool calls
 * in real-time as they arrive from the API.
 */
public interface StreamHandler {

    /** Called with each text delta as it arrives from the streaming API. */
    default void onContent(String delta) {
    }

    /** Called with reasoning content deltas (from reasoning models). */
    default void onReasoning(String delta) {
    }

    /** Called when a tool call is detected during streaming. */
    default void onToolCall(String toolName) {
    }

    /** Called when streaming for a single response is complete. */
    default void onComplete() {
    }
}
