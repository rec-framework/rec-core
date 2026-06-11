package net.kimleo.rec.agent.tui;

import net.kimleo.rec.agent.Agent;
import net.kimleo.rec.agent.StreamHandler;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.EndOfFileException;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Interactive TUI for chatting with the Rec agent.
 * Uses JLine3 for readline-like input with history.
 */
public final class AgentTui {

    private static final Logger LOGGER = LoggerFactory.getLogger(AgentTui.class);
    private static final String RESET = "\u001b[0m";
    private static final String DIM = "\u001b[2m";
    private static final String CYAN = "\u001b[36m";
    private static final String GREEN = "\u001b[32m";
    private static final String YELLOW = "\u001b[33m";
    private static final String MAGENTA = "\u001b[35m";
    private static final String PROMPT = "rec> ";
    private static volatile boolean running = true;

    private AgentTui() {
    }

    /** Start the agent TUI from command line args. */
    public static void start(String[] args) throws IOException {
        TuiConfig config = parseArgs(args);
        Agent agent;
        try {
            agent = new Agent(config.model, config.systemPrompt,
                    config.workspace, config.baseUrl, config.apiKey);
        } catch (IllegalStateException e) {
            System.err.println("Error: OPENAI_API_KEY environment variable is not set.");
            System.err.println("Please set it before starting the agent:");
            System.err.println("  export OPENAI_API_KEY=sk-...");
            System.err.println("Or use --api-key flag:");
            System.err.println("  ./rec --agent --api-key sk-...");
            return;
        }
        runRepl(agent, config);
        agent.close();
    }

    private static void runRepl(Agent agent, TuiConfig config) throws IOException {
        Terminal terminal = TerminalBuilder.builder().system(true).build();
        LineReader reader = LineReaderBuilder.builder().terminal(terminal).build();
        String flags = buildFlagsString(config);
        System.out.println(CYAN + "Rec Agent TUI (model: " + config.model + flags + ")" + RESET);
        System.out.println(DIM + "Type /help for commands, /quit to exit" + RESET);

        while (running) {
            String input = readInput(reader);
            if (input == null) {
                break;
            }
            String trimmed = input.trim();
            handleCommand(trimmed, agent);
            if (running) {
                processChat(agent, trimmed, config);
            }
        }
    }

    private static String buildFlagsString(TuiConfig config) {
        StringBuilder sb = new StringBuilder();
        if (!config.streaming) {
            sb.append(", no-streaming");
        }
        if (!config.reasoning) {
            sb.append(", no-reasoning");
        }
        return sb.toString();
    }

    private static String readInput(LineReader reader) {
        try {
            String line = reader.readLine(PROMPT);
            if (line == null) {
                return null;
            }
            return handleMultiLine(line, reader);
        } catch (UserInterruptException e) {
            return null;
        } catch (EndOfFileException e) {
            return null;
        }
    }

    private static String handleMultiLine(String line, LineReader reader) {
        StringBuilder sb = new StringBuilder(line);
        while (sb.toString().endsWith("\\")) {
            sb.setLength(sb.length() - 1);
            String next = reader.readLine("... ");
            if (next == null) {
                break;
            }
            sb.append('\n').append(next);
        }
        return sb.toString();
    }

    private static void handleCommand(String input, Agent agent) {
        if (input.isEmpty()) {
            return;
        }
        if (!input.startsWith("/")) {
            return;
        }
        switch (input.toLowerCase()) {
            case "/quit":
            case "/exit":
                running = false;
                break;
            case "/help":
                printHelp();
                break;
            case "/tools":
                printTools(agent);
                break;
            case "/skills":
                printSkills(agent);
                break;
            case "/clear":
                System.out.println(DIM + "(conversation reset)" + RESET);
                break;
            default:
                System.out.println(DIM + "Unknown command: " + input + RESET);
                break;
        }
    }

    private static void processChat(Agent agent, String input, TuiConfig config) {
        if (input.isEmpty() || input.startsWith("/")) {
            return;
        }
        try {
            if (config.streaming) {
                StreamHandler handler = config.reasoning
                        ? new TuiStreamHandler() : new TuiStreamHandlerNoReasoning();
                agent.chatStreaming(input, handler);
                System.out.println();
            } else {
                String response = agent.chat(input);
                printToolCalls(agent);
                if (config.reasoning) {
                    printReasoning(agent);
                }
                System.out.println(GREEN + response + RESET);
            }
        } catch (Exception e) {
            LOGGER.error("Chat error", e);
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void printToolCalls(Agent agent) {
        String toolInfo = agent.lastToolCalls();
        if (!toolInfo.isEmpty()) {
            System.out.println(DIM + toolInfo + RESET);
        }
    }

    private static void printReasoning(Agent agent) {
        String reasoning = agent.lastReasoning();
        if (reasoning != null && !reasoning.isEmpty()) {
            System.out.println(YELLOW + "[Reasoning]" + RESET);
            System.out.println(DIM + reasoning + RESET);
        }
    }

    private static void printHelp() {
        System.out.println("Commands:");
        System.out.println("  /quit, /exit  - Exit the TUI");
        System.out.println("  /tools        - List available tools");
        System.out.println("  /skills       - List discovered skills");
        System.out.println("  /clear        - Reset conversation");
        System.out.println("  /help         - Show this help");
    }

    private static void printTools(Agent agent) {
        System.out.println("Available tools:");
        for (var tool : agent.toolRegistry().list()) {
            System.out.println("  - " + tool.name() + ": " + tool.description());
        }
    }

    private static void printSkills(Agent agent) {
        var skills = agent.skillRegistry().listDiscovered();
        if (skills.isEmpty()) {
            System.out.println("No skills discovered.");
            return;
        }
        System.out.println("Discovered skills:");
        for (var skill : skills) {
            String status = skill.isActivated() ? " (active)" : "";
            System.out.println("  - " + skill.name() + ": " + skill.description() + status);
        }
    }

    private static TuiConfig parseArgs(String[] args) {
        TuiConfig config = new TuiConfig();
        for (int i = 0; i < args.length; i++) {
            String next = (i + 1 < args.length) ? args[i + 1] : null;
            i += applyArg(config, args[i], next);
        }
        return config;
    }

    private static int applyArg(TuiConfig config, String arg, String next) {
        switch (arg) {
            case "--model": config.model = next; return 1;
            case "--workspace": config.workspace = Paths.get(next); return 1;
            case "--system-prompt": config.systemPrompt = next; return 1;
            case "--base-url": config.baseUrl = next; return 1;
            case "--api-key": config.apiKey = next; return 1;
            case "--disable-streaming": config.streaming = false; return 0;
            case "--disable-reasoning": config.reasoning = false; return 0;
            default: return 0;
        }
    }

    private static final class TuiConfig {
        String model = "gpt-4o";
        Path workspace = Paths.get(".");
        String systemPrompt = null;
        String baseUrl = null;
        String apiKey = null;
        boolean streaming = true;
        boolean reasoning = true;
    }

    /** Stream handler that displays reasoning, tool calls, and content in real-time. */
    private static class TuiStreamHandler implements StreamHandler {
        private boolean inReasoning = false;
        private boolean inContent = false;

        @Override
        public void onReasoning(String delta) {
            if (!inReasoning) {
                System.out.println(YELLOW + "[Thinking]" + RESET);
                System.out.print(DIM);
                inReasoning = true;
            }
            System.out.print(delta);
        }

        @Override
        public void onContent(String delta) {
            if (inReasoning) {
                System.out.print(RESET + "\n");
                inReasoning = false;
            }
            if (!inContent) {
                System.out.print(GREEN);
                inContent = true;
            }
            System.out.print(delta);
        }

        @Override
        public void onToolCall(String toolName) {
            if (inContent) {
                System.out.print(RESET + "\n");
                inContent = false;
            }
            if (inReasoning) {
                System.out.print(RESET + "\n");
                inReasoning = false;
            }
            System.out.println(DIM + "[Tool: " + toolName + "]" + RESET);
        }

        @Override
        public void onComplete() {
            if (inContent) {
                System.out.print(RESET);
                inContent = false;
            }
            if (inReasoning) {
                System.out.print(RESET);
                inReasoning = false;
            }
        }
    }

    /** Stream handler without reasoning output. */
    private static class TuiStreamHandlerNoReasoning implements StreamHandler {
        private boolean inContent = false;

        @Override
        public void onContent(String delta) {
            if (!inContent) {
                System.out.print(GREEN);
                inContent = true;
            }
            System.out.print(delta);
        }

        @Override
        public void onToolCall(String toolName) {
            if (inContent) {
                System.out.print(RESET + "\n");
                inContent = false;
            }
            System.out.println(DIM + "[Tool: " + toolName + "]" + RESET);
        }

        @Override
        public void onComplete() {
            if (inContent) {
                System.out.print(RESET);
                inContent = false;
            }
        }
    }
}
