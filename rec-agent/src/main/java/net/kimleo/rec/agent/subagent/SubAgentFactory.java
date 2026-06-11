package net.kimleo.rec.agent.subagent;

import com.openai.client.OpenAIClient;
import net.kimleo.rec.agent.tool.ToolRegistry;

import java.util.ArrayList;
import java.util.List;

/**
 * Factory that creates Plan and Explore sub-agents.
 */
public final class SubAgentFactory {

    private SubAgentFactory() {
    }

    public static List<SubAgent> all(OpenAIClient client, String model, ToolRegistry parentTools) {
        List<SubAgent> agents = new ArrayList<>();
        agents.add(new PlanSubAgent(client, model, parentTools));
        agents.add(new ExploreSubAgent(client, model, parentTools));
        return agents;
    }
}
