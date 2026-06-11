package net.kimleo.rec.agent.tool.builtin;

import net.kimleo.rec.agent.tool.Tool;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Find files matching a glob pattern.
 */
public class GlobTool implements Tool {

    private final Path workspace;

    public GlobTool(Path workspace) {
        this.workspace = workspace;
    }

    @Override
    public String name() {
        return "Glob";
    }

    @Override
    public String description() {
        return "Find files matching a glob pattern, return matching paths.";
    }

    @Override
    public Map<String, Object> parametersSchema() {
        Map<String, Object> props = new LinkedHashMap<>();
        props.put("pattern", Map.of("type", "string", "description", "Glob pattern"));
        props.put("path", Map.of("type", "string", "description", "Base path (defaults to workspace)"));

        Map<String, Object> schema = new LinkedHashMap<>();
        schema.put("type", "object");
        schema.put("properties", props);
        schema.put("required", List.of("pattern"));
        return schema;
    }

    @Override
    public String execute(Map<String, Object> arguments) {
        String pattern = (String) arguments.get("pattern");
        String basePath = (String) arguments.get("path");
        Path base = basePath != null ? workspace.resolve(basePath) : workspace;

        PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:" + pattern);
        try (Stream<Path> paths = Files.walk(base.normalize())) {
            Path normalizedBase = base.normalize();
            String result = paths
                    .filter(p -> matchesPattern(matcher, normalizedBase, p))
                    .limit(200)
                    .map(Path::toString)
                    .collect(Collectors.joining("\n"));
            return result.isEmpty() ? "No files found." : result;
        } catch (IOException e) {
            return "Error: " + e.getMessage();
        }
    }

    private static boolean matchesPattern(PathMatcher matcher, Path base, Path path) {
        Path relative = base.relativize(path);
        return matcher.matches(relative) || matcher.matches(relative.getFileName());
    }
}
