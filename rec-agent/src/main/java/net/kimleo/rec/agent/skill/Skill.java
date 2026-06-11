package net.kimleo.rec.agent.skill;

import java.nio.file.Path;
import java.util.Map;

/**
 * Represents a parsed agent skill following the agentskills.io spec.
 * Supports progressive disclosure: metadata loaded eagerly, body lazy.
 */
public class Skill {

    private final String name;
    private final String description;
    private final Path directory;
    private final Map<String, Object> metadata;
    private String instructions;
    private boolean activated;

    public Skill(String name, String description, Path directory,
                 Map<String, Object> metadata) {
        this.name = name;
        this.description = description;
        this.directory = directory;
        this.metadata = metadata;
        this.activated = false;
    }

    public String name() {
        return name;
    }

    public String description() {
        return description;
    }

    public Path directory() {
        return directory;
    }

    public Map<String, Object> metadata() {
        return metadata;
    }

    public boolean isActivated() {
        return activated;
    }

    public String instructions() {
        return instructions;
    }

    public void activate(String body) {
        this.instructions = body;
        this.activated = true;
    }

    /** Returns metadata summary for system prompt context. */
    public String summary() {
        return "- " + name + ": " + description;
    }
}
