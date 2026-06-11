package net.kimleo.rec.agent.tool.builtin;

import net.kimleo.rec.agent.tool.Tool;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Read file content with optional line range.
 */
public class ReadTool implements Tool {

    private final Path workspace;

    public ReadTool(Path workspace) {
        this.workspace = workspace;
    }

    @Override
    public String name() {
        return "Read";
    }

    @Override
    public String description() {
        return "Read file content. Supports optional line range via startLine and endLine.";
    }

    @Override
    public Map<String, Object> parametersSchema() {
        Map<String, Object> props = new LinkedHashMap<>();
        props.put("path", Map.of("type", "string", "description", "File path to read"));
        props.put("startLine", Map.of("type", "integer", "description", "Start line (1-based)"));
        props.put("endLine", Map.of("type", "integer", "description", "End line (inclusive)"));

        Map<String, Object> schema = new LinkedHashMap<>();
        schema.put("type", "object");
        schema.put("properties", props);
        schema.put("required", List.of("path"));
        return schema;
    }

    @Override
    public String execute(Map<String, Object> arguments) {
        String pathStr = (String) arguments.get("path");
        Path filePath = workspace.resolve(pathStr).normalize();

        try {
            List<String> lines = Files.readAllLines(filePath);
            int start = getIntArg(arguments, "startLine", 1) - 1;
            int end = getIntArg(arguments, "endLine", lines.size());
            start = Math.max(0, Math.min(start, lines.size()));
            end = Math.max(start, Math.min(end, lines.size()));

            StringBuilder sb = new StringBuilder();
            for (int i = start; i < end; i++) {
                sb.append(i + 1).append("\t").append(lines.get(i)).append("\n");
            }
            return sb.toString();
        } catch (IOException e) {
            return "Error reading file: " + e.getMessage();
        }
    }

    private static int getIntArg(Map<String, Object> args, String key, int defaultValue) {
        Object val = args.get(key);
        if (val instanceof Number) {
            return ((Number) val).intValue();
        }
        return defaultValue;
    }
}
