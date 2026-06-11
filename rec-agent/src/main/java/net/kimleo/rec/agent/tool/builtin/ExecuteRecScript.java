package net.kimleo.rec.agent.tool.builtin;

import net.kimleo.rec.agent.tool.Tool;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Execute a Rec JS script (inline code or file) and capture output.
 * Uses the in-process Scripting engine via reflection to avoid
 * compile-time dependency on rec-scripting.
 */
public class ExecuteRecScript implements Tool {

    private static final int MAX_OUTPUT = 30_000;
    private final Path workspace;

    public ExecuteRecScript(Path workspace) {
        this.workspace = workspace;
    }

    @Override
    public String name() {
        return "ExecuteRecScript";
    }

    @Override
    public String description() {
        return "Execute a Rec data pipeline script written in JavaScript. "
                + "Provide either 'script' (inline JS code) or 'path' (path to a .js file). "
                + "The script has full access to the Rec pipeline API via require(\"rec\"). "
                + "Returns captured stdout output.";
    }

    @Override
    public Map<String, Object> parametersSchema() {
        Map<String, Object> props = new LinkedHashMap<>();
        props.put("script", Map.of(
                "type", "string",
                "description", "Inline JavaScript code to execute. "
                        + "Use require(\"rec\") to access the Rec API."));
        props.put("path", Map.of(
                "type", "string",
                "description", "Path to a .js script file to execute"));

        Map<String, Object> schema = new LinkedHashMap<>();
        schema.put("type", "object");
        schema.put("properties", props);
        schema.put("required", List.of());
        return schema;
    }

    @Override
    public String execute(Map<String, Object> arguments) {
        String script = (String) arguments.get("script");
        String pathStr = (String) arguments.get("path");

        if (pathStr != null && !pathStr.isEmpty()) {
            return executeFile(pathStr);
        }
        if (script != null && !script.isEmpty()) {
            return executeInline(script);
        }
        return "Error: either 'script' or 'path' must be provided.";
    }

    private String executeFile(String pathStr) {
        Path scriptPath = workspace.resolve(pathStr).normalize();
        if (!Files.exists(scriptPath)) {
            return "Error: script file not found: " + pathStr;
        }
        return runScript(scriptPath.toFile(), pathStr);
    }

    private String executeInline(String script) {
        try {
            Path tmpFile = Files.createTempFile("rec-agent-", ".js");
            Files.writeString(tmpFile, script);
            try {
                return runScript(tmpFile.toFile(), tmpFile.toString());
            } finally {
                Files.deleteIfExists(tmpFile);
            }
        } catch (Exception e) {
            return "Error executing inline script: " + e.getMessage();
        }
    }

    private static String runScript(File file, String filename) {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream captured = new ByteArrayOutputStream();
        try {
            System.setOut(new PrintStream(captured));
            Class<?> scripting = Class.forName("net.kimleo.rec.scripting.Scripting");
            scripting.getMethod("runfile", File.class, String.class,
                            boolean.class, String.class)
                    .invoke(null, file, filename, false, "");
        } catch (ClassNotFoundException e) {
            return "Error: rec-scripting module not on classpath";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        } finally {
            System.setOut(originalOut);
        }
        String output = captured.toString();
        if (output.length() > MAX_OUTPUT) {
            return output.substring(0, MAX_OUTPUT) + "\n... [truncated]";
        }
        return output.isEmpty() ? "Script executed successfully (no output)." : output;
    }
}
