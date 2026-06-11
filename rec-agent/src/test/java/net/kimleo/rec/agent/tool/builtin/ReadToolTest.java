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

public class ReadToolTest {

    @Rule
    public TemporaryFolder tempDir = new TemporaryFolder();

    private ReadTool tool;
    private Path workspace;

    @Before
    public void setUp() {
        workspace = tempDir.getRoot().toPath();
        tool = new ReadTool(workspace);
    }

    @Test
    public void testReadFile() throws IOException {
        Path file = workspace.resolve("test.txt");
        Files.writeString(file, "hello\nworld\n");

        String result = tool.execute(Map.of("path", "test.txt"));
        assertTrue(result.contains("hello"));
        assertTrue(result.contains("world"));
    }

    @Test
    public void testReadFileWithLineRange() throws IOException {
        Path file = workspace.resolve("lines.txt");
        Files.writeString(file, "line1\nline2\nline3\nline4\n");

        String result = tool.execute(Map.of("path", "lines.txt",
                "startLine", 2, "endLine", 3));
        assertTrue(result.contains("line2"));
        assertTrue(result.contains("line3"));
        assertFalse(result.contains("line1"));
        assertFalse(result.contains("line4"));
    }

    @Test
    public void testReadMissingFile() {
        String result = tool.execute(Map.of("path", "nonexistent.txt"));
        assertTrue(result.startsWith("Error"));
    }

    @Test
    public void testToolMetadata() {
        assertEquals("Read", tool.name());
        assertNotNull(tool.description());
        assertNotNull(tool.parametersSchema());
    }
}
