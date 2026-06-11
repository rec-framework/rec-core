package net.kimleo.rec.agent.tool;

import java.util.Map;

/**
 * Interface for tools that an agent can invoke.
 */
public interface Tool {

    /** Tool name, used as the function name in OpenAI tool definitions. */
    String name();

    /** Human-readable description for the model. */
    String description();

    /** JSON Schema as a Map describing the tool's parameters. */
    Map<String, Object> parametersSchema();

    /** Execute with parsed arguments, return string result. */
    String execute(Map<String, Object> arguments);
}
