package net.kimleo.rec.agent.context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Loads AGENTS.md content from the workspace root if present.
 */
public final class AgentsMdLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(AgentsMdLoader.class);
    private static final String AGENTS_MD = "AGENTS.md";

    private AgentsMdLoader() {
    }

    /** Load AGENTS.md content from the given workspace directory. */
    public static String load(Path workspace) {
        Path agentsMd = workspace.resolve(AGENTS_MD);
        if (!Files.isRegularFile(agentsMd)) {
            return "";
        }
        try {
            String content = Files.readString(agentsMd);
            LOGGER.info("Loaded AGENTS.md ({} chars)", content.length());
            return content;
        } catch (IOException e) {
            LOGGER.warn("Failed to read AGENTS.md at {}", agentsMd, e);
            return "";
        }
    }
}
