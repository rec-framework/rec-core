package net.kimleo.rec.agent.tool.builtin;

import net.kimleo.rec.agent.tool.Tool;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Write content to a file (create or overwrite).
 */
public class WriteTool implements Tool {

    private final Path workspace;

    public WriteTool(Path workspace) {
        this.workspace = workspace;
    }

    @Override
    public String name() {
        return "Write";
    }

    @Override
    public String description() {
        return "Write content to a file. Creates the file if it does not exist, or overwrites.";
    }

    @Override
    public Map<String, Object> parametersSchema() {
        Map<String, Object> props = new LinkedHashMap<>();
        props.put("path", Map.of("type", "string", "description", "File path to write"));
        props.put("content", Map.of("type", "string", "description", "Content to write"));

        Map<String, Object> schema = new LinkedHashMap<>();
        schema.put("type", "object");
        schema.put("properties", props);
        schema.put("required", List.of("path", "content"));
        return schema;
    }

    @Override
    public String execute(Map<String, Object> arguments) {
        String pathStr = (String) arguments.get("path");
        String content = (String) arguments.get("content");
        Path filePath = workspace.resolve(pathStr).normalize();

        if (!filePath.startsWith(workspace.normalize())) {
            return "Error: write path must be within workspace";
        }
        try {
            Files.createDirectories(filePath.getParent());
            Files.writeString(filePath, content);
            return "File written: " + filePath + " (" + content.length() + " chars)";
        } catch (IOException e) {
            return "Error writing file: " + e.getMessage();
        }
    }
}
