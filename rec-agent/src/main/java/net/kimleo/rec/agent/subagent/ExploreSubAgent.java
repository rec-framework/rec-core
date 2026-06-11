package net.kimleo.rec.agent.subagent;

import com.openai.client.OpenAIClient;
import net.kimleo.rec.agent.tool.ToolRegistry;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Explore sub-agent: investigates the codebase and returns findings.
 * Has access to Read, Glob, Grep, Bash for exploration.
 */
public class ExploreSubAgent extends SubAgent {

    private static final List<String> ALLOWED_TOOLS = List.of("Read", "Glob", "Grep", "Bash");

    public ExploreSubAgent(OpenAIClient client, String model, ToolRegistry parentTools) {
        super(client, model, filterTools(parentTools, ALLOWED_TOOLS));
    }

    @Override
    public String name() {
        return "Explore";
    }

    @Override
    public String description() {
        return "Explore the codebase and return findings. "
                + "Use for investigation and understanding code structure.";
    }

    @Override
    public Map<String, Object> parametersSchema() {
        Map<String, Object> props = new LinkedHashMap<>();
        props.put("task", Map.of("type", "string",
                "description", "What to explore or investigate"));

        Map<String, Object> schema = new LinkedHashMap<>();
        schema.put("type", "object");
        schema.put("properties", props);
        schema.put("required", List.of("task"));
        return schema;
    }

    @Override
    protected String systemPrompt() {
        return "You are a codebase exploration assistant. Given a question or area to explore, "
                + "use Read, Glob, Grep, and Bash tools to investigate the codebase thoroughly. "
                + "Return a comprehensive summary of your findings.";
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
