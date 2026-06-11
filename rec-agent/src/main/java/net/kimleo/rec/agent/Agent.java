package net.kimleo.rec.agent;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.core.JsonString;
import com.openai.core.JsonValue;
import com.openai.core.http.StreamResponse;
import com.openai.helpers.ChatCompletionAccumulator;
import com.openai.models.chat.completions.ChatCompletion;
import com.openai.models.chat.completions.ChatCompletionChunk;
import com.openai.models.chat.completions.ChatCompletionCreateParams;
import com.openai.models.chat.completions.ChatCompletionMessageFunctionToolCall;
import com.openai.models.chat.completions.ChatCompletionMessageToolCall;
import com.openai.models.chat.completions.ChatCompletionToolMessageParam;
import net.kimleo.rec.agent.context.WorkspaceContext;
import net.kimleo.rec.agent.mcp.McpToolBridge;
import net.kimleo.rec.agent.skill.SkillRegistry;
import net.kimleo.rec.agent.subagent.SubAgentFactory;
import net.kimleo.rec.agent.tool.Tool;
import net.kimleo.rec.agent.tool.ToolRegistry;
import net.kimleo.rec.agent.tool.builtin.BuiltinTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Core agent that manages conversation with OpenAI, tool calling,
 * skills, and workspace context.
 */
public class Agent {

    private static final Logger LOGGER = LoggerFactory.getLogger(Agent.class);
    private static final int MAX_ITERATIONS = 50;
    private static final String DEFAULT_SYSTEM_PROMPT =
            "You are a helpful assistant with access to tools.";

    private final OpenAIClient client;
    private final String model;
    private final String systemPrompt;
    private final ToolRegistry toolRegistry;
    private final SkillRegistry skillRegistry;
    private final Path workspace;
    private final List<McpToolBridge> mcpBridges;
    private String lastToolCalls;
    private String lastReasoning;

    public Agent(String model, String systemPrompt, Path workspace) {
        this(model, systemPrompt, workspace, null, null);
    }

    /** Create an agent with optional custom base URL and API key. */
    public Agent(String model, String systemPrompt, Path workspace,
                 String baseUrl, String apiKey) {
        this.client = buildClient(baseUrl, apiKey);
        this.model = model;
        this.workspace = workspace;
        this.toolRegistry = new ToolRegistry();
        this.skillRegistry = new SkillRegistry();
        this.mcpBridges = new ArrayList<>();
        this.systemPrompt = systemPrompt != null ? systemPrompt : DEFAULT_SYSTEM_PROMPT;
        initTools();
        initWorkspaceContext();
    }

    private static OpenAIClient buildClient(String baseUrl, String apiKey) {
        var builder = OpenAIOkHttpClient.builder();
        if (baseUrl != null) {
            builder.baseUrl(baseUrl);
        }
        if (apiKey != null) {
            builder.apiKey(apiKey);
        }
        if (baseUrl != null || apiKey != null) {
            return builder.build();
        }
        return OpenAIOkHttpClient.fromEnv();
    }

    private void initTools() {
        for (Tool tool : BuiltinTools.all(workspace)) {
            toolRegistry.register(tool);
        }
        for (Tool tool : SubAgentFactory.all(client, model, toolRegistry)) {
            toolRegistry.register(tool);
        }
    }

    private void initWorkspaceContext() {
        skillRegistry.addBuiltinSkills("skills");
        WorkspaceContext ctx = WorkspaceContext.load(workspace);
        ctx.registerSkills(skillRegistry);
    }

    /** Send a message and get the agent's response (non-streaming). */
    public String chat(String message) {
        return chatStreaming(message, null);
    }

    /** Send a message with streaming, invoking the handler as content arrives. */
    public String chatStreaming(String message, StreamHandler handler) {
        ChatCompletionCreateParams.Builder builder = buildParams(message);
        return executeWithToolLoop(builder, handler);
    }

    private ChatCompletionCreateParams.Builder buildParams(String message) {
        ChatCompletionCreateParams.Builder builder = ChatCompletionCreateParams.builder()
                .model(model)
                .addSystemMessage(buildSystemPrompt())
                .addUserMessage(message);
        builder.tools(toolRegistry.toOpenAiTools());
        return builder;
    }

    private String executeWithToolLoop(ChatCompletionCreateParams.Builder builder,
                                       StreamHandler handler) {
        for (int i = 0; i < MAX_ITERATIONS; i++) {
            ChatCompletion completion = streamAndAccumulate(builder.build(), handler);
            var message = completion.choices().get(0).message();
            builder.addMessage(message);
            lastReasoning = extractReasoning(message._additionalProperties()).orElse("");

            List<ChatCompletionMessageToolCall> toolCalls =
                    message.toolCalls().orElse(List.of());
            if (toolCalls.isEmpty()) {
                lastToolCalls = "";
                if (handler != null) {
                    handler.onComplete();
                }
                return message.content().orElse("");
            }

            lastToolCalls = formatToolCalls(toolCalls);
            if (handler != null) {
                emitToolCalls(toolCalls, handler);
            }
            handleToolCalls(builder, toolCalls);
        }
        return "Error: max iterations reached";
    }

    private ChatCompletion streamAndAccumulate(ChatCompletionCreateParams params,
                                               StreamHandler handler) {
        ChatCompletionAccumulator accumulator = ChatCompletionAccumulator.create();
        try (StreamResponse<ChatCompletionChunk> stream =
                     client.chat().completions().createStreaming(params)) {
            stream.stream().forEach(chunk -> {
                accumulator.accumulate(chunk);
                emitChunkDelta(chunk, handler);
            });
        }
        return accumulator.chatCompletion();
    }

    private static void emitChunkDelta(ChatCompletionChunk chunk, StreamHandler handler) {
        if (handler == null || chunk.choices().isEmpty()) {
            return;
        }
        var delta = chunk.choices().get(0).delta();
        delta.content().ifPresent(handler::onContent);
        extractReasoning(delta._additionalProperties()).ifPresent(handler::onReasoning);
    }

    private static java.util.Optional<String> extractReasoning(
            Map<String, JsonValue> props) {
        JsonValue val = props.get("reasoning_content");
        if (val instanceof JsonString) {
            return java.util.Optional.of(((JsonString) val).value());
        }
        return java.util.Optional.empty();
    }

    private void emitToolCalls(List<ChatCompletionMessageToolCall> toolCalls,
                               StreamHandler handler) {
        for (ChatCompletionMessageToolCall call : toolCalls) {
            handler.onToolCall(call.asFunction().function().name());
        }
    }

    private String formatToolCalls(List<ChatCompletionMessageToolCall> toolCalls) {
        StringBuilder sb = new StringBuilder();
        for (ChatCompletionMessageToolCall call : toolCalls) {
            ChatCompletionMessageFunctionToolCall fc = call.asFunction();
            sb.append("[Tool: ").append(fc.function().name()).append("] ");
        }
        return sb.toString();
    }

    private void handleToolCalls(ChatCompletionCreateParams.Builder builder,
                                 List<ChatCompletionMessageToolCall> toolCalls) {
        for (ChatCompletionMessageToolCall call : toolCalls) {
            String result = executeToolCall(call);
            builder.addMessage(ChatCompletionToolMessageParam.builder()
                    .toolCallId(call.asFunction().id())
                    .content(result)
                    .build());
        }
    }

    private String executeToolCall(ChatCompletionMessageToolCall call) {
        ChatCompletionMessageFunctionToolCall fc = call.asFunction();
        String name = fc.function().name();
        Tool tool = toolRegistry.get(name);
        if (tool == null) {
            return "Error: tool '" + name + "' not found";
        }
        LOGGER.info("Executing tool: {}", name);
        Map<String, Object> args = parseArgs(fc.function().arguments());
        try {
            return tool.execute(args);
        } catch (Exception e) {
            LOGGER.error("Tool '{}' failed", name, e);
            return "Error: " + e.getMessage();
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

    /** Build the full system prompt including AGENTS.md, user prompt, and skills. */
    public String buildSystemPrompt() {
        StringBuilder sb = new StringBuilder();
        String agentsMd = WorkspaceContext.load(workspace).agentsMdContent();
        if (!agentsMd.isEmpty()) {
            sb.append(agentsMd).append("\n\n");
        }
        sb.append(systemPrompt);
        String skillsCtx = skillRegistry.buildSkillsContext();
        if (!skillsCtx.isEmpty()) {
            sb.append("\n\n").append(skillsCtx);
        }
        return sb.toString();
    }

    public String lastToolCalls() {
        return lastToolCalls;
    }

    public String lastReasoning() {
        return lastReasoning;
    }

    public ToolRegistry toolRegistry() {
        return toolRegistry;
    }

    public SkillRegistry skillRegistry() {
        return skillRegistry;
    }

    public Path workspace() {
        return workspace;
    }

    public String model() {
        return model;
    }

    /** Add an MCP server via stdio and register its tools. */
    public void addMcpServer(String command, String... args) {
        McpToolBridge bridge = McpToolBridge.stdio(command, args);
        mcpBridges.add(bridge);
        for (Tool tool : bridge.discoverTools()) {
            toolRegistry.register(tool);
        }
    }

    public void addTool(Tool tool) {
        toolRegistry.register(tool);
    }

    public void removeTool(String name) {
        toolRegistry.unregister(name);
    }

    public void close() {
        for (McpToolBridge bridge : mcpBridges) {
            bridge.close();
        }
    }
}
