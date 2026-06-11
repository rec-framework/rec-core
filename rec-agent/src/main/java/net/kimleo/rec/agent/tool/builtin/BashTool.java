package net.kimleo.rec.agent.tool.builtin;

import net.kimleo.rec.agent.tool.Tool;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Execute a shell command and return stdout+stderr.
 */
public class BashTool implements Tool {

    private static final int DEFAULT_TIMEOUT = 30;
    private static final int MAX_OUTPUT = 30000;
    private static final Set<String> RISKY_PATTERNS = Set.of(
            "rm -rf", "rm -fr", "sudo", "mkfs", "dd if=");

    private final Path workspace;

    public BashTool(Path workspace) {
        this.workspace = workspace;
    }

    @Override
    public String name() {
        return "Bash";
    }

    @Override
    public String description() {
        return "Execute a shell command in the workspace directory. Returns stdout and stderr.";
    }

    @Override
    public Map<String, Object> parametersSchema() {
        Map<String, Object> props = new LinkedHashMap<>();
        props.put("command", Map.of("type", "string", "description", "Shell command to execute"));
        props.put("timeout", Map.of("type", "integer", "description", "Timeout in seconds (default 30)"));

        Map<String, Object> schema = new LinkedHashMap<>();
        schema.put("type", "object");
        schema.put("properties", props);
        schema.put("required", List.of("command"));
        return schema;
    }

    @Override
    public String execute(Map<String, Object> arguments) {
        String command = (String) arguments.get("command");
        int timeout = getIntArg(arguments, "timeout", DEFAULT_TIMEOUT);

        if (isRisky(command)) {
            return "Error: command contains potentially dangerous patterns: " + command;
        }

        try {
            return runProcess(command, timeout);
        } catch (Exception e) {
            return "Error executing command: " + e.getMessage();
        }
    }

    private String runProcess(String command, int timeout) throws Exception {
        ProcessBuilder pb = new ProcessBuilder("/bin/sh", "-c", command)
                .directory(workspace.toFile())
                .redirectErrorStream(true);
        Process process = pb.start();

        String output;
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()))) {
            output = reader.lines().collect(Collectors.joining("\n"));
        }

        boolean finished = process.waitFor(timeout, TimeUnit.SECONDS);
        if (!finished) {
            process.destroyForcibly();
            return "Error: command timed out after " + timeout + "s\n" + truncate(output);
        }

        String result = truncate(output);
        int exitCode = process.exitValue();
        return exitCode != 0 ? result + "\n[exit code: " + exitCode + "]" : result;
    }

    private static boolean isRisky(String command) {
        String lower = command.toLowerCase();
        return RISKY_PATTERNS.stream().anyMatch(lower::contains);
    }

    private static String truncate(String output) {
        if (output.length() > MAX_OUTPUT) {
            return output.substring(0, MAX_OUTPUT) + "\n... [truncated]";
        }
        return output;
    }

    private static int getIntArg(Map<String, Object> args, String key, int defaultValue) {
        Object val = args.get(key);
        if (val instanceof Number) {
            return ((Number) val).intValue();
        }
        return defaultValue;
    }
}
