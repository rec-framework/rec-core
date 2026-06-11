package net.kimleo.rec.agent.skill;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Loads skills from directories containing SKILL.md files.
 * Supports progressive disclosure: only YAML frontmatter parsed on discovery.
 */
public final class SkillLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(SkillLoader.class);
    private static final String SKILL_FILE = "SKILL.md";
    private static final String FRONTMATTER_DELIMITER = "---";
    private static final ObjectMapper YAML = new ObjectMapper(new YAMLFactory());

    private SkillLoader() {
    }

    /** Discover all skills in the given directories. */
    public static Map<String, Skill> discover(Path... dirs) {
        Map<String, Skill> skills = new LinkedHashMap<>();
        for (Path dir : dirs) {
            if (!Files.isDirectory(dir)) {
                continue;
            }
            discoverInDirectory(dir, skills);
        }
        return skills;
    }

    private static void discoverInDirectory(Path dir, Map<String, Skill> skills) {
        try (Stream<Path> stream = Files.walk(dir, 2)) {
            stream.filter(p -> p.getFileName().toString().equals(SKILL_FILE))
                    .forEach(skillMd -> parseFrontmatter(skillMd, skills));
        } catch (IOException e) {
            LOGGER.warn("Failed to scan directory for skills: {}", dir, e);
        }
    }

    @SuppressWarnings("unchecked")
    private static void parseFrontmatter(Path skillMd, Map<String, Skill> skills) {
        try {
            String content = Files.readString(skillMd);
            if (!content.startsWith(FRONTMATTER_DELIMITER)) {
                LOGGER.warn("Skill file missing frontmatter: {}", skillMd);
                return;
            }
            int end = content.indexOf(FRONTMATTER_DELIMITER, 3);
            if (end < 0) {
                LOGGER.warn("Skill file has unclosed frontmatter: {}", skillMd);
                return;
            }
            String yaml = content.substring(3, end).trim();
            Map<String, Object> meta = YAML.readValue(yaml, LinkedHashMap.class);
            String name = requireField(meta, "name", skillMd);
            String desc = requireField(meta, "description", skillMd);
            if (name == null || desc == null) {
                return;
            }
            Path skillDir = skillMd.getParent();
            skills.put(name, new Skill(name, desc, skillDir, meta));
            LOGGER.info("Discovered skill: {} ({})", name, skillDir);
        } catch (IOException e) {
            LOGGER.warn("Failed to parse skill file: {}", skillMd, e);
        }
    }

    private static String requireField(Map<String, Object> meta, String key, Path file) {
        Object value = meta.get(key);
        if (value == null || value.toString().isBlank()) {
            LOGGER.warn("Skill file missing '{}' field: {}", key, file);
            return null;
        }
        return value.toString();
    }

    /** Parse the full body (markdown after frontmatter) of a skill file. */
    public static String parseBody(Path skillDir) {
        Path skillMd = skillDir.resolve(SKILL_FILE);
        try {
            String content = Files.readString(skillMd);
            int end = content.indexOf(FRONTMATTER_DELIMITER, 3);
            if (end < 0) {
                return "";
            }
            return content.substring(end + 3).trim();
        } catch (IOException e) {
            LOGGER.warn("Failed to read skill body: {}", skillMd, e);
            return "";
        }
    }
}
