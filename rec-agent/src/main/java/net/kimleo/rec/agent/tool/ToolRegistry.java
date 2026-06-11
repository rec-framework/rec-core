package net.kimleo.rec.agent.tool;

import com.openai.core.JsonValue;
import com.openai.models.FunctionDefinition;
import com.openai.models.FunctionParameters;
import com.openai.models.chat.completions.ChatCompletionFunctionTool;
import com.openai.models.chat.completions.ChatCompletionTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Registry managing the collection of tools available to an agent.
 */
public class ToolRegistry {

    private static final Logger LOGGER = LoggerFactory.getLogger(ToolRegistry.class);

    private final Map<String, Tool> tools = new LinkedHashMap<>();

    public void register(Tool tool) {
        LOGGER.info("Registered tool: {}", tool.name());
        tools.put(tool.name(), tool);
    }

    public void unregister(String name) {
        LOGGER.info("Unregistered tool: {}", name);
        tools.remove(name);
    }

    public Tool get(String name) {
        return tools.get(name);
    }

    public boolean has(String name) {
        return tools.containsKey(name);
    }

    public List<Tool> list() {
        return new ArrayList<>(tools.values());
    }

    public List<ChatCompletionTool> toOpenAiTools() {
        List<ChatCompletionTool> result = new ArrayList<>();
        for (Tool tool : tools.values()) {
            result.add(toChatCompletionTool(tool));
        }
        return result;
    }

    private static ChatCompletionTool toChatCompletionTool(Tool tool) {
        FunctionParameters params = buildParameters(tool.parametersSchema());
        FunctionDefinition function = FunctionDefinition.builder()
                .name(tool.name())
                .description(tool.description())
                .parameters(params)
                .build();
        ChatCompletionFunctionTool functionTool = ChatCompletionFunctionTool.builder()
                .function(function)
                .build();
        return ChatCompletionTool.ofFunction(functionTool);
    }

    private static FunctionParameters buildParameters(Map<String, Object> schema) {
        FunctionParameters.Builder builder = FunctionParameters.builder();
        for (Map.Entry<String, Object> entry : schema.entrySet()) {
            builder.putAdditionalProperty(entry.getKey(), JsonValue.from(entry.getValue()));
        }
        return builder.build();
    }
}
