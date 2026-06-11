package net.kimleo.rec.agent.context;

import net.kimleo.rec.agent.skill.SkillRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Loads workspace-level context: AGENTS.md and .agents/skills directory.
 */
public class WorkspaceContext {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkspaceContext.class);
    private static final String SKILLS_DIR = ".agents/skills";

    private final String agentsMdContent;
    private final Path workspace;

    public WorkspaceContext(Path workspace, String agentsMdContent) {
        this.workspace = workspace;
        this.agentsMdContent = agentsMdContent;
    }

    /** Load workspace context from the given directory. */
    public static WorkspaceContext load(Path workspace) {
        String agentsMd = AgentsMdLoader.load(workspace);
        return new WorkspaceContext(workspace, agentsMd);
    }

    public String agentsMdContent() {
        return agentsMdContent;
    }

    public Path workspace() {
        return workspace;
    }

    /** Register auto-discovered skills from .agents/skills into the registry. */
    public void registerSkills(SkillRegistry registry) {
        Path skillsDir = workspace.resolve(SKILLS_DIR);
        if (Files.isDirectory(skillsDir)) {
            registry.addDirectories(skillsDir);
            LOGGER.info("Auto-discovered skills from {}", skillsDir);
        }
    }
}
