package net.kimleo.rec.agent.context;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AgentsMdLoaderTest {

    @Rule
    public TemporaryFolder tempDir = new TemporaryFolder();

    @Test
    public void testLoadAgentsMd() throws IOException {
        Files.writeString(tempDir.getRoot().toPath().resolve("AGENTS.md"),
                "# Project Instructions\nBe helpful.");
        String content = AgentsMdLoader.load(tempDir.getRoot().toPath());
        assertTrue(content.contains("Project Instructions"));
    }

    @Test
    public void testMissingAgentsMd() {
        String content = AgentsMdLoader.load(tempDir.getRoot().toPath());
        assertEquals("", content);
    }

    @Test
    public void testNonExistentDirectory() {
        Path nope = tempDir.getRoot().toPath().resolve("nope");
        String content = AgentsMdLoader.load(nope);
        assertEquals("", content);
    }
}
