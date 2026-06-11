package net.kimleo.rec.agent.skill;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SkillLoaderTest {

    @Rule
    public TemporaryFolder tempDir = new TemporaryFolder();

    @Test
    public void testDiscoverSkill() throws IOException {
        Path skillDir = tempDir.newFolder("my-skill").toPath();
        Files.writeString(skillDir.resolve("SKILL.md"),
                "---\nname: my-skill\ndescription: A test skill\n---\nInstructions here.");

        Map<String, Skill> skills = SkillLoader.discover(tempDir.getRoot().toPath());
        assertEquals(1, skills.size());
        assertTrue(skills.containsKey("my-skill"));
        assertEquals("A test skill", skills.get("my-skill").description());
    }

    @Test
    public void testParseBody() throws IOException {
        Path skillDir = tempDir.newFolder("body-skill").toPath();
        Files.writeString(skillDir.resolve("SKILL.md"),
                "---\nname: body-skill\ndescription: Has body\n---\nThis is the body.");

        String body = SkillLoader.parseBody(skillDir);
        assertEquals("This is the body.", body);
    }

    @Test
    public void testMissingFrontmatter() throws IOException {
        Path skillDir = tempDir.newFolder("bad-skill").toPath();
        Files.writeString(skillDir.resolve("SKILL.md"), "No frontmatter here");

        Map<String, Skill> skills = SkillLoader.discover(tempDir.getRoot().toPath());
        assertEquals(0, skills.size());
    }

    @Test
    public void testMissingNameField() throws IOException {
        Path skillDir = tempDir.newFolder("no-name").toPath();
        Files.writeString(skillDir.resolve("SKILL.md"),
                "---\ndescription: Missing name\n---\nBody.");

        Map<String, Skill> skills = SkillLoader.discover(tempDir.getRoot().toPath());
        assertEquals(0, skills.size());
    }

    @Test
    public void testEmptyDirectory() {
        Map<String, Skill> skills = SkillLoader.discover(tempDir.getRoot().toPath());
        assertEquals(0, skills.size());
    }

    @Test
    public void testNonExistentDirectory() {
        Path nope = tempDir.getRoot().toPath().resolve("does-not-exist");
        Map<String, Skill> skills = SkillLoader.discover(nope);
        assertEquals(0, skills.size());
    }
}
