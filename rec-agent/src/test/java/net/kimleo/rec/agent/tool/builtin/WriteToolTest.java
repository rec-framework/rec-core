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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class WriteToolTest {

    @Rule
    public TemporaryFolder tempDir = new TemporaryFolder();

    private WriteTool tool;
    private Path workspace;

    @Before
    public void setUp() {
        workspace = tempDir.getRoot().toPath();
        tool = new WriteTool(workspace);
    }

    @Test
    public void testWriteFile() {
        String result = tool.execute(Map.of("path", "output.txt", "content", "test data"));
        assertFalse(result.startsWith("Error"));

        Path file = workspace.resolve("output.txt");
        assertTrue(Files.exists(file));
    }

    @Test
    public void testOverwriteFile() throws IOException {
        Path file = workspace.resolve("existing.txt");
        Files.writeString(file, "old content");

        tool.execute(Map.of("path", "existing.txt", "content", "new content"));
        assertEquals("new content", Files.readString(file));
    }

    @Test
    public void testSandboxEnforcement() {
        String result = tool.execute(Map.of("path", "/etc/passwd", "content", "hack"));
        assertTrue(result.startsWith("Error"));
    }

    @Test
    public void testToolMetadata() {
        assertEquals("Write", tool.name());
        assertNotNull(tool.description());
    }
}
