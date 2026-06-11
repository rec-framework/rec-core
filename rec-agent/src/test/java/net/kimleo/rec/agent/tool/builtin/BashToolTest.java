package net.kimleo.rec.agent.tool.builtin;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.nio.file.Path;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class BashToolTest {

    @Rule
    public TemporaryFolder tempDir = new TemporaryFolder();

    private BashTool tool;
    private Path workspace;

    @Before
    public void setUp() {
        workspace = tempDir.getRoot().toPath();
        tool = new BashTool(workspace);
    }

    @Test
    public void testSimpleCommand() {
        String result = tool.execute(Map.of("command", "echo hello"));
        assertTrue(result.contains("hello"));
    }

    @Test
    public void testRiskyCommand() {
        String result = tool.execute(Map.of("command", "rm -rf /"));
        assertTrue(result.contains("dangerous"));
    }

    @Test
    public void testSudoBlocked() {
        String result = tool.execute(Map.of("command", "sudo ls"));
        assertTrue(result.contains("dangerous"));
    }

    @Test
    public void testToolMetadata() {
        assertEquals("Bash", tool.name());
        assertNotNull(tool.description());
    }
}
