package net.kimleo.rec.agent.tool.builtin;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class EditToolTest {

    @Rule
    public TemporaryFolder tempDir = new TemporaryFolder();

    private EditTool tool;
    private Path workspace;

    @Before
    public void setUp() {
        workspace = tempDir.getRoot().toPath();
        tool = new EditTool(workspace);
    }

    @Test
    public void testEditFile() throws IOException {
        Path file = workspace.resolve("edit.txt");
        Files.writeString(file, "hello world");

        String result = tool.execute(Map.of(
                "path", "edit.txt", "oldText", "world", "newText", "java"));
        assertFalse(result.startsWith("Error"));
        assertEquals("hello java", Files.readString(file));
    }

    @Test
    public void testEditNoMatch() throws IOException {
        Path file = workspace.resolve("edit.txt");
        Files.writeString(file, "hello world");

        String result = tool.execute(Map.of(
                "path", "edit.txt", "oldText", "xyz", "newText", "abc"));
        assertTrue(result.contains("not found"));
    }

    @Test
    public void testSandboxEnforcement() {
        String result = tool.execute(Map.of(
                "path", "/etc/passwd", "oldText", "a", "newText", "b"));
        assertTrue(result.startsWith("Error"));
    }
}
