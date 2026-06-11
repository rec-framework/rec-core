package net.kimleo.rec.agent.skill;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Registry for managing discovered skills with progressive disclosure.
 */
public class SkillRegistry {

    private static final Logger LOGGER = LoggerFactory.getLogger(SkillRegistry.class);

    private final Map<String, Skill> skills = new LinkedHashMap<>();

    public void addDirectories(Path... dirs) {
        Map<String, Skill> discovered = SkillLoader.discover(dirs);
        skills.putAll(discovered);
    }

    public List<Skill> listDiscovered() {
        return new ArrayList<>(skills.values());
    }

    public Skill get(String name) {
        return skills.get(name);
    }

    /** Activate a skill by loading its full body. Returns the instructions. */
    public String activate(String name) {
        Skill skill = skills.get(name);
        if (skill == null) {
            return "Error: skill '" + name + "' not found";
        }
        if (!skill.isActivated()) {
            String body = SkillLoader.parseBody(skill.directory());
            skill.activate(body);
            LOGGER.info("Activated skill: {}", name);
        }
        return skill.instructions();
    }

    /** Build context string listing all discovered skills. */
    public String buildSkillsContext() {
        if (skills.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder("Available skills:\n");
        for (Skill skill : skills.values()) {
            sb.append(skill.summary()).append('\n');
        }
        sb.append("\nTo activate a skill, use the ActivateSkill tool.");
        return sb.toString();
    }

    /** Load built-in skills from classpath resources. */
    public void addBuiltinSkills(String resourcePath) {
        try {
            Path tmpDir = Files.createTempDirectory("rec-agent-skills-");
            extractResourceDirectory(resourcePath, tmpDir);
            addDirectories(tmpDir);
            tmpDir.toFile().deleteOnExit();
        } catch (IOException e) {
            LOGGER.warn("Failed to load built-in skills from: {}", resourcePath, e);
        }
    }

    private static void extractResourceDirectory(String resourcePath, Path targetDir)
            throws IOException {
        Enumeration<URL> urls = SkillRegistry.class.getClassLoader()
                .getResources(resourcePath);
        while (urls.hasMoreElements()) {
            URL url = urls.nextElement();
            if (!"file".equals(url.getProtocol())) {
                continue;
            }
            Path srcDir = Path.of(url.getPath());
            if (!Files.isDirectory(srcDir)) {
                continue;
            }
            try (var stream = Files.walk(srcDir, 3)) {
                stream.filter(p -> p.getFileName().toString().equals("SKILL.md"))
                        .forEach(skillMd -> copySkillTree(skillMd, targetDir));
            }
        }
    }

    private static void copySkillTree(Path skillMd, Path targetRoot) {
        try {
            Path skillDir = skillMd.getParent();
            String dirName = skillDir.getFileName().toString();
            Path target = targetRoot.resolve(dirName);
            Files.createDirectories(target);
            try (var stream = Files.walk(skillDir, 1)) {
                stream.forEach(p -> {
                    try {
                        Path rel = skillDir.relativize(p);
                        Path dst = target.resolve(rel);
                        if (Files.isDirectory(p)) {
                            Files.createDirectories(dst);
                        } else {
                            Files.copy(p, dst, StandardCopyOption.REPLACE_EXISTING);
                        }
                    } catch (IOException e) {
                        // skip
                    }
                });
            }
        } catch (IOException e) {
            // skip
        }
    }
}
