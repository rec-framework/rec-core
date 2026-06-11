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

public class GrepToolTest {

    @Rule
    public TemporaryFolder tempDir = new TemporaryFolder();

    private GrepTool tool;
    private Path workspace;

    @Before
    public void setUp() throws IOException {
        workspace = tempDir.getRoot().toPath();
        tool = new GrepTool(workspace);
        Files.writeString(workspace.resolve("hello.txt"), "hello world\nhello java\n");
        Files.writeString(workspace.resolve("other.txt"), "goodbye world\n");
    }

    @Test
    public void testGrepPattern() {
        String result = tool.execute(Map.of("pattern", "hello"));
        assertTrue(result.contains("hello.txt"));
        assertFalse(result.contains("goodbye"));
    }

    @Test
    public void testGrepWithInclude() {
        String result = tool.execute(Map.of("pattern", "world", "include", "*.txt"));
        assertTrue(result.contains("hello.txt"));
        assertTrue(result.contains("other.txt"));
    }

    @Test
    public void testGrepNoMatch() {
        String result = tool.execute(Map.of("pattern", "zzzzz"));
        assertTrue(result.contains("No matches"));
    }
}
