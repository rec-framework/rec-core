package net.kimleo.rec.agent.tool.builtin;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class GlobToolTest {

    @Rule
    public TemporaryFolder tempDir = new TemporaryFolder();

    private GlobTool tool;
    private Path workspace;

    @Before
    public void setUp() throws IOException {
        workspace = tempDir.getRoot().toPath();
        tool = new GlobTool(workspace);
        Files.writeString(workspace.resolve("a.txt"), "a");
        Files.writeString(workspace.resolve("b.java"), "b");
        Files.createDirectories(workspace.resolve("sub"));
        Files.writeString(workspace.resolve("sub/c.txt"), "c");
    }

    @Test
    public void testGlobPattern() {
        String result = tool.execute(Map.of("pattern", "*.txt"));
        assertTrue(result.contains("a.txt"));
        assertFalse(result.contains("b.java"));
    }

    @Test
    public void testGlobSubdirectory() {
        String result = tool.execute(Map.of("pattern", "sub/*.txt"));
        assertFalse(result.contains("a.txt"));
        assertTrue(result.contains("c.txt"));
    }

    @Test
    public void testGlobNoMatch() {
        String result = tool.execute(Map.of("pattern", "*.xyz"));
        assertTrue(result.contains("No files"));
    }
}
