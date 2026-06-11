package net.kimleo.rec.agent.tool.builtin;

import net.kimleo.rec.agent.tool.Tool;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Replace oldText with newText in a file.
 */
public class EditTool implements Tool {

    private final Path workspace;

    public EditTool(Path workspace) {
        this.workspace = workspace;
    }

    @Override
    public String name() {
        return "Edit";
    }

    @Override
    public String description() {
        return "Replace oldText with newText in a file. Fails if oldText is not found.";
    }

    @Override
    public Map<String, Object> parametersSchema() {
        Map<String, Object> props = new LinkedHashMap<>();
        props.put("path", Map.of("type", "string", "description", "File path to edit"));
        props.put("oldText", Map.of("type", "string", "description", "Text to find"));
        props.put("newText", Map.of("type", "string", "description", "Replacement text"));

        Map<String, Object> schema = new LinkedHashMap<>();
        schema.put("type", "object");
        schema.put("properties", props);
        schema.put("required", List.of("path", "oldText", "newText"));
        return schema;
    }

    @Override
    public String execute(Map<String, Object> arguments) {
        String pathStr = (String) arguments.get("path");
        String oldText = (String) arguments.get("oldText");
        String newText = (String) arguments.get("newText");
        Path filePath = workspace.resolve(pathStr).normalize();

        if (!filePath.startsWith(workspace.normalize())) {
            return "Error: edit path must be within workspace";
        }
        try {
            String content = Files.readString(filePath);
            if (!content.contains(oldText)) {
                return "Error: oldText not found in file";
            }
            String updated = content.replace(oldText, newText);
            Files.writeString(filePath, updated);
            return "File edited: " + filePath;
        } catch (IOException e) {
            return "Error editing file: " + e.getMessage();
        }
    }
}
