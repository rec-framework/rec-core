package net.kimleo.rec.plugin;

import net.kimleo.rec.agent.Agent;
import net.kimleo.rec.agent.skill.Skill;
import net.kimleo.rec.agent.tool.Tool;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

/**
 * RecPlugin implementation exposing Agent functionality to JS scripts.
 * Public methods are accessible as agent.xxx() in the JS scope.
 */
public class AgentModule implements RecPlugin {

    @Override
    public String name() {
        return "agent";
    }

    @Override
    public Object module() {
        return this;
    }

    public Agent createAgent(String model) {
        return new Agent(model, null, Paths.get("."));
    }

    public Agent createAgent(String model, String systemPrompt) {
        return new Agent(model, systemPrompt, Paths.get("."));
    }

    public Agent createAgent(String model, String systemPrompt, String workspacePath) {
        return new Agent(model, systemPrompt, Paths.get(workspacePath));
    }

    public String chat(Agent agent, String message) {
        return agent.chat(message);
    }

    public void addTool(Agent agent, String name, String description,
                        Map<String, Object> schema,
                        java.util.function.Function<Map<String, Object>, String> fn) {
        agent.addTool(new Tool() {
            @Override
            public String name() {
                return name;
            }

            @Override
            public String description() {
                return description;
            }

            @Override
            public Map<String, Object> parametersSchema() {
                return schema;
            }

            @Override
            public String execute(Map<String, Object> arguments) {
                return fn.apply(arguments);
            }
        });
    }

    public void addMcpServer(Agent agent, String command, String... args) {
        agent.addMcpServer(command, args);
    }

    public void addSkillDirectories(Agent agent, String... dirs) {
        Path[] paths = new Path[dirs.length];
        for (int i = 0; i < dirs.length; i++) {
            paths[i] = Paths.get(dirs[i]);
        }
        agent.skillRegistry().addDirectories(paths);
    }

    public String activateSkill(Agent agent, String skillName) {
        return agent.skillRegistry().activate(skillName);
    }

    public void removeBuiltinTool(Agent agent, String toolName) {
        agent.removeTool(toolName);
    }

    public List<Skill> listSkills(Agent agent) {
        return agent.skillRegistry().listDiscovered();
    }

    public List<String> listTools(Agent agent) {
        return agent.toolRegistry().list().stream()
                .map(Tool::name).toList();
    }

    public void close(Agent agent) {
        agent.close();
    }
}
