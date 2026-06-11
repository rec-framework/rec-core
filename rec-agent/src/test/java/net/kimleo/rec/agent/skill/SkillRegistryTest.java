package net.kimleo.rec.agent.skill;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SkillRegistryTest {

    @Rule
    public TemporaryFolder tempDir = new TemporaryFolder();

    @Test
    public void testAddDirectories() throws IOException {
        Path skillDir = tempDir.newFolder("skill-a").toPath();
        Files.writeString(skillDir.resolve("SKILL.md"),
                "---\nname: skill-a\ndescription: Skill A\n---\nBody A.");

        SkillRegistry registry = new SkillRegistry();
        registry.addDirectories(tempDir.getRoot().toPath());
        assertEquals(1, registry.listDiscovered().size());
    }

    @Test
    public void testActivate() throws IOException {
        Path skillDir = tempDir.newFolder("activatable").toPath();
        Files.writeString(skillDir.resolve("SKILL.md"),
                "---\nname: activatable\ndescription: Can activate\n---\nActivate me!");

        SkillRegistry registry = new SkillRegistry();
        registry.addDirectories(tempDir.getRoot().toPath());

        String instructions = registry.activate("activatable");
        assertEquals("Activate me!", instructions);

        Skill skill = registry.get("activatable");
        assertTrue(skill.isActivated());
    }

    @Test
    public void testActivateMissing() {
        SkillRegistry registry = new SkillRegistry();
        String result = registry.activate("nonexistent");
        assertTrue(result.contains("Error"));
    }

    @Test
    public void testBuildSkillsContext() throws IOException {
        Path skillDir = tempDir.newFolder("ctx-skill").toPath();
        Files.writeString(skillDir.resolve("SKILL.md"),
                "---\nname: ctx-skill\ndescription: Context skill\n---\nBody.");

        SkillRegistry registry = new SkillRegistry();
        registry.addDirectories(tempDir.getRoot().toPath());

        String context = registry.buildSkillsContext();
        assertTrue(context.contains("ctx-skill"));
        assertTrue(context.contains("Context skill"));
    }

    @Test
    public void testEmptyRegistryContext() {
        SkillRegistry registry = new SkillRegistry();
        assertEquals("", registry.buildSkillsContext());
    }
}
