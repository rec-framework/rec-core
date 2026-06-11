package net.kimleo.rec.agent.tool.builtin;

import net.kimleo.rec.agent.tool.Tool;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Regex search in files, return matches with context.
 */
public class GrepTool implements Tool {

    private static final int MAX_RESULTS = 200;
    private final Path workspace;

    public GrepTool(Path workspace) {
        this.workspace = workspace;
    }

    @Override
    public String name() {
        return "Grep";
    }

    @Override
    public String description() {
        return "Search for a regex pattern in files. Returns matching lines with file and line number.";
    }

    @Override
    public Map<String, Object> parametersSchema() {
        Map<String, Object> props = new LinkedHashMap<>();
        props.put("pattern", Map.of("type", "string", "description", "Regex pattern"));
        props.put("path", Map.of("type", "string", "description", "Directory to search"));
        props.put("include", Map.of("type", "string", "description", "File glob filter, e.g. *.java"));

        Map<String, Object> schema = new LinkedHashMap<>();
        schema.put("type", "object");
        schema.put("properties", props);
        schema.put("required", List.of("pattern"));
        return schema;
    }

    @Override
    public String execute(Map<String, Object> arguments) {
        String patternStr = (String) arguments.get("pattern");
        String searchPath = (String) arguments.get("path");
        String include = (String) arguments.get("include");
        Path base = searchPath != null ? workspace.resolve(searchPath) : workspace;

        Pattern regex = Pattern.compile(patternStr);
        StringBuilder sb = new StringBuilder();
        int count = 0;

        try (Stream<Path> files = Files.walk(base.normalize())) {
            for (Path file : files.filter(Files::isRegularFile).toList()) {
                if (count >= MAX_RESULTS) break;
                if (include != null && !file.getFileName().toString().matches(
                        include.replace("*", ".*").replace("?", "."))) {
                    continue;
                }
                count = searchFile(file, regex, sb, count);
            }
        } catch (IOException e) {
            return "Error: " + e.getMessage();
        }
        return count == 0 ? "No matches found." : sb.toString();
    }

    private int searchFile(Path file, Pattern regex, StringBuilder sb, int count) {
        try {
            List<String> lines = Files.readAllLines(file);
            for (int i = 0; i < lines.size() && count < MAX_RESULTS; i++) {
                Matcher m = regex.matcher(lines.get(i));
                if (m.find()) {
                    sb.append(file).append(":").append(i + 1).append(": ")
                            .append(lines.get(i).trim()).append("\n");
                    count++;
                }
            }
        } catch (IOException ignored) {
            // skip unreadable files
        }
        return count;
    }
}
