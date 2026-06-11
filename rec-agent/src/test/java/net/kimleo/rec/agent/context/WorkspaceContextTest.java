package net.kimleo.rec.agent.context;

import net.kimleo.rec.agent.skill.SkillRegistry;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class WorkspaceContextTest {

    @Rule
    public TemporaryFolder tempDir = new TemporaryFolder();

    @Test
    public void testLoadWithAgentsMd() throws IOException {
        Files.writeString(tempDir.getRoot().toPath().resolve("AGENTS.md"),
                "Project context here.");
        WorkspaceContext ctx = WorkspaceContext.load(tempDir.getRoot().toPath());
        assertTrue(ctx.agentsMdContent().contains("Project context"));
    }

    @Test
    public void testLoadWithoutAgentsMd() {
        WorkspaceContext ctx = WorkspaceContext.load(tempDir.getRoot().toPath());
        assertEquals("", ctx.agentsMdContent());
    }

    @Test
    public void testRegisterSkills() throws IOException {
        Path skillsDir = tempDir.newFolder(".agents", "skills", "test-skill").toPath();
        Files.writeString(skillsDir.resolve("SKILL.md"),
                "---\nname: test-skill\ndescription: Test\n---\nBody.");

        WorkspaceContext ctx = WorkspaceContext.load(tempDir.getRoot().toPath());
        SkillRegistry registry = new SkillRegistry();
        ctx.registerSkills(registry);

        assertEquals(1, registry.listDiscovered().size());
    }

    @Test
    public void testRegisterSkillsNoDir() {
        WorkspaceContext ctx = WorkspaceContext.load(tempDir.getRoot().toPath());
        SkillRegistry registry = new SkillRegistry();
        ctx.registerSkills(registry);
        assertEquals(0, registry.listDiscovered().size());
    }
}
