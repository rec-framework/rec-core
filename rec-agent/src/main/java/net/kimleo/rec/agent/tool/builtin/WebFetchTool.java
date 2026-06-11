package net.kimleo.rec.agent.tool.builtin;

import net.kimleo.rec.agent.tool.Tool;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Fetch URL content.
 */
public class WebFetchTool implements Tool {

    private static final int TIMEOUT_MS = 15000;
    private static final int MAX_CHARS = 20000;

    @Override
    public String name() {
        return "WebFetch";
    }

    @Override
    public String description() {
        return "Fetch content from a URL. Returns the text content.";
    }

    @Override
    public Map<String, Object> parametersSchema() {
        Map<String, Object> props = new LinkedHashMap<>();
        props.put("url", Map.of("type", "string", "description", "URL to fetch"));
        props.put("query", Map.of("type", "string",
                "description", "Optional query to extract relevant sections"));

        Map<String, Object> schema = new LinkedHashMap<>();
        schema.put("type", "object");
        schema.put("properties", props);
        schema.put("required", List.of("url"));
        return schema;
    }

    @Override
    public String execute(Map<String, Object> arguments) {
        String url = (String) arguments.get("url");
        try {
            HttpURLConnection conn = (HttpURLConnection) URI.create(url).toURL().openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(TIMEOUT_MS);
            conn.setReadTimeout(TIMEOUT_MS);
            conn.setRequestProperty("User-Agent", "RecAgent/1.0");

            int code = conn.getResponseCode();
            if (code != 200) {
                return "HTTP error: " + code;
            }

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()))) {
                String content = reader.lines()
                        .collect(Collectors.joining("\n"));
                if (content.length() > MAX_CHARS) {
                    content = content.substring(0, MAX_CHARS) + "\n... [truncated]";
                }
                return content;
            }
        } catch (Exception e) {
            return "Error fetching URL: " + e.getMessage();
        }
    }
}
