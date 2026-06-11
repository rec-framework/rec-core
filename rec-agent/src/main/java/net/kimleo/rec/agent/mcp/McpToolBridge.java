package net.kimleo.rec.agent.mcp;

import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.ServerParameters;
import io.modelcontextprotocol.client.transport.StdioClientTransport;
import io.modelcontextprotocol.json.jackson3.JacksonMcpJsonMapper;
import io.modelcontextprotocol.spec.McpSchema;
import net.kimleo.rec.agent.tool.Tool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.jackson.databind.json.JsonMapper;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Bridges MCP server tools into the agent's tool registry.
 * Wraps McpSyncClient and adapts each MCP tool to the Tool interface.
 */
public class McpToolBridge implements AutoCloseable {

    private static final Logger LOGGER = LoggerFactory.getLogger(McpToolBridge.class);

    private final McpSyncClient client;

    public McpToolBridge(McpSyncClient client) {
        this.client = client;
    }

    /** Create a bridge connected to an MCP server via stdio. */
    public static McpToolBridge stdio(String command, String... args) {
        ServerParameters params = ServerParameters.builder(command)
                .args(args)
                .build();
        JacksonMcpJsonMapper mapper = new JacksonMcpJsonMapper(JsonMapper.builder().build());
        StdioClientTransport transport = new StdioClientTransport(params, mapper);
        McpSyncClient syncClient = McpClient.sync(transport).build();
        syncClient.initialize();
        LOGGER.info("MCP client initialized via stdio: {}", command);
        return new McpToolBridge(syncClient);
    }

    /** Discover all tools from the MCP server and adapt them. */
    public List<Tool> discoverTools() {
        McpSchema.ListToolsResult result = client.listTools();
        List<Tool> tools = new ArrayList<>();
        for (McpSchema.Tool mcpTool : result.tools()) {
            tools.add(new McpToolAdapter(mcpTool, client));
            LOGGER.info("Discovered MCP tool: {}", mcpTool.name());
        }
        return tools;
    }

    @Override
    public void close() {
        client.close();
    }

    /** Adapts a single MCP tool to the agent Tool interface. */
    private static final class McpToolAdapter implements Tool {

        private final McpSchema.Tool mcpTool;
        private final McpSyncClient client;

        McpToolAdapter(McpSchema.Tool mcpTool, McpSyncClient client) {
            this.mcpTool = mcpTool;
            this.client = client;
        }

        @Override
        public String name() {
            return mcpTool.name();
        }

        @Override
        public String description() {
            return mcpTool.description() != null ? mcpTool.description() : "";
        }

        @Override
        public Map<String, Object> parametersSchema() {
            McpSchema.JsonSchema schema = mcpTool.inputSchema();
            if (schema == null) {
                return Map.of("type", "object", "properties", Map.of());
            }
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("type", schema.type() != null ? schema.type() : "object");
            if (schema.properties() != null) {
                result.put("properties", schema.properties());
            }
            if (schema.required() != null) {
                result.put("required", schema.required());
            }
            return result;
        }

        @Override
        public String execute(Map<String, Object> arguments) {
            McpSchema.CallToolRequest request =
                    new McpSchema.CallToolRequest(mcpTool.name(), arguments);
            McpSchema.CallToolResult result = client.callTool(request);
            return formatResult(result);
        }

        private String formatResult(McpSchema.CallToolResult result) {
            StringBuilder sb = new StringBuilder();
            for (McpSchema.Content content : result.content()) {
                if (content instanceof McpSchema.TextContent text) {
                    sb.append(text.text());
                }
            }
            if (Boolean.TRUE.equals(result.isError())) {
                return "Error: " + sb;
            }
            return sb.toString();
        }
    }
}
