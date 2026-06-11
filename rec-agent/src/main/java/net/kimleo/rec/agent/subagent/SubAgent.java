package net.kimleo.rec.agent.subagent;

import com.openai.client.OpenAIClient;
import com.openai.core.JsonString;
import com.openai.core.JsonValue;
import com.openai.models.chat.completions.ChatCompletion;
import com.openai.models.chat.completions.ChatCompletionCreateParams;
import com.openai.models.chat.completions.ChatCompletionMessageFunctionToolCall;
import com.openai.models.chat.completions.ChatCompletionMessageToolCall;
import com.openai.models.chat.completions.ChatCompletionToolMessageParam;
import net.kimleo.rec.agent.tool.Tool;
import net.kimleo.rec.agent.tool.ToolRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Abstract base for sub-agents that appear as tools in the parent agent.
 * Each sub-agent runs its own conversation loop with a subset of tools.
 */
public abstract class SubAgent implements Tool {

    private static final Logger LOGGER = LoggerFactory.getLogger(SubAgent.class);
    private static final int MAX_ITERATIONS = 20;

    protected final OpenAIClient client;
    protected final String model;
    protected final ToolRegistry toolRegistry;

    protected SubAgent(OpenAIClient client, String model, ToolRegistry toolRegistry) {
        this.client = client;
        this.model = model;
        this.toolRegistry = toolRegistry;
    }

    @Override
    public String execute(Map<String, Object> arguments) {
        String task = (String) arguments.getOrDefault("task", "");
        LOGGER.info("SubAgent '{}' executing task: {}", name(), task);

        ChatCompletionCreateParams.Builder builder = ChatCompletionCreateParams.builder()
                .model(model)
                .addSystemMessage(systemPrompt())
                .addUserMessage(task);
        builder.tools(toolRegistry.toOpenAiTools());

        for (int i = 0; i < MAX_ITERATIONS; i++) {
            ChatCompletion response = client.chat().completions().create(builder.build());
            var message = response.choices().get(0).message();
            builder.addMessage(message);

            List<ChatCompletionMessageToolCall> toolCalls =
                    message.toolCalls().orElse(List.of());
            if (toolCalls.isEmpty()) {
                logReasoning(message);
                return message.content().orElse("");
            }

            logReasoning(message);
            handleToolCalls(builder, toolCalls);
        }
        return "Error: max iterations reached";
    }

    private void handleToolCalls(ChatCompletionCreateParams.Builder builder,
                                 List<ChatCompletionMessageToolCall> toolCalls) {
        for (ChatCompletionMessageToolCall call : toolCalls) {
            ChatCompletionMessageFunctionToolCall fc = call.asFunction();
            Tool tool = toolRegistry.get(fc.function().name());
            if (tool == null) {
                builder.addMessage(ChatCompletionToolMessageParam.builder()
                        .toolCallId(fc.id())
                        .content("Error: tool '" + fc.function().name() + "' not found")
                        .build());
                continue;
            }
            Map<String, Object> args = parseArgs(fc.function().arguments());
            String result = tool.execute(args);
            builder.addMessage(ChatCompletionToolMessageParam.builder()
                    .toolCallId(fc.id())
                    .content(result)
                    .build());
        }
    }

    private static void logReasoning(
            com.openai.models.chat.completions.ChatCompletionMessage message) {
        Map<String, JsonValue> props = message._additionalProperties();
        JsonValue val = props.get("reasoning_content");
        if (val instanceof JsonString) {
            LOGGER.debug("SubAgent reasoning: {}", ((JsonString) val).value());
        }
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> parseArgs(String json) {
        try {
            return new com.fasterxml.jackson.databind.ObjectMapper()
                    .readValue(json, LinkedHashMap.class);
        } catch (Exception e) {
            return Map.of();
        }
    }

    /** System prompt specific to this sub-agent. */
    protected abstract String systemPrompt();
}
