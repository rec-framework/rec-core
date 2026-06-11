package net.kimleo.rec.agent.tool.builtin;

import net.kimleo.rec.agent.tool.Tool;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Factory that creates all built-in tools (file tools + Rec script execution).
 */
public final class BuiltinTools {

    private BuiltinTools() {
    }

    public static List<Tool> all(Path workspace) {
        List<Tool> tools = new ArrayList<>();
        tools.addAll(fileTools(workspace));
        tools.add(new ExecuteRecScript(workspace));
        return tools;
    }

    public static List<Tool> fileTools(Path workspace) {
        List<Tool> tools = new ArrayList<>();
        tools.add(new ReadTool(workspace));
        tools.add(new WriteTool(workspace));
        tools.add(new EditTool(workspace));
        tools.add(new GlobTool(workspace));
        tools.add(new GrepTool(workspace));
        tools.add(new WebFetchTool());
        tools.add(new BashTool(workspace));
        return tools;
    }
}
