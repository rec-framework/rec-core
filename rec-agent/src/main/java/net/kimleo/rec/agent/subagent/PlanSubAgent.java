package net.kimleo.rec.agent.subagent;

import com.openai.client.OpenAIClient;
import net.kimleo.rec.agent.tool.ToolRegistry;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Plan sub-agent: produces structured implementation plans.
 * Has access to Read, Glob, Grep for codebase exploration.
 */
public class PlanSubAgent extends SubAgent {

    private static final List<String> ALLOWED_TOOLS = List.of("Read", "Glob", "Grep");

    public PlanSubAgent(OpenAIClient client, String model, ToolRegistry parentTools) {
        super(client, model, filterTools(parentTools, ALLOWED_TOOLS));
    }

    @Override
    public String name() {
        return "Plan";
    }

    @Override
    public String description() {
        return "Create a structured implementation plan for a task. "
                + "Use for multi-step tasks requiring planning.";
    }

    @Override
    public Map<String, Object> parametersSchema() {
        Map<String, Object> props = new LinkedHashMap<>();
        props.put("task", Map.of("type", "string",
                "description", "The task to plan for"));

        Map<String, Object> schema = new LinkedHashMap<>();
        schema.put("type", "object");
        schema.put("properties", props);
        schema.put("required", List.of("task"));
        return schema;
    }

    @Override
    protected String systemPrompt() {
        return "You are a planning assistant. Given a task, produce a detailed, structured "
                + "implementation plan with steps, files to modify, and rationale. "
                + "Use Read, Glob, and Grep tools to explore the codebase before planning.";
    }

    private static ToolRegistry filterTools(ToolRegistry parent, List<String> allowed) {
        ToolRegistry filtered = new ToolRegistry();
        for (var tool : parent.list()) {
            if (allowed.contains(tool.name())) {
                filtered.register(tool);
            }
        }
        return filtered;
    }
}
