package net.kimleo.rec;

import net.kimleo.rec.common.exception.InitializationException;
import net.kimleo.rec.scripting.Scripting;
import net.kimleo.rec.utils.Records;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Arrays;

import static net.kimleo.rec.util.Sys.die;

public class App {

    private final static Logger LOGGER = LoggerFactory.getLogger(App.class);
    public static final String MSG_NO_SIZE_SPECIFIED =
            "Cannot infer binary file column size, please specify one.";

    public static void main(String[] args) throws Exception {
        LOGGER.info("Application started");
        LOGGER.info("Args:" + Arrays.toString(args));
        System.out.println("=> Rec v2");

        if (args.length <= 0) {
            die("You should provide a script file or using commands");
        }

        if ("--agent".equals(args[0])) {
            startAgentTui(args);
            LOGGER.info("Application ended");
            return;
        }

        if (args.length == 1) {
            execute(args[0], false, "");
        }

        if (args.length >= 2) {
            dispatch(args);
        }

        LOGGER.info("Application ended");
    }

    private static void dispatch(String[] args) throws Exception {
        String command = args[0];
        String retryFile = null;
        int retry = Arrays.asList(args).indexOf("--retry");
        if (retry > 0 && args.length >= retry) {
            retryFile = args[retry + 1];
        }
        switch (command) {
            case "js":
            case "script":
                execute(args[1], retryFile != null, retryFile);
                break;
            case "dump":
                dumpBinary(args);
                break;
            default:
                break;
        }
    }

    private static void dumpBinary(String[] args) throws Exception {
        String fileName = args[1];
        File binFile = new File(fileName);
        int size;
        if (args.length == 3) {
            size = Integer.parseInt(args[2]);
        } else {
            size = inferSizeFromFileName(fileName);
        }
        Records.dump(binFile, size);
    }

    private static int inferSizeFromFileName(String fileName) {
        return Integer.parseInt(
                Arrays.stream(fileName.split("\\."))
                        .findFirst()
                        .orElseThrow(() ->
                                new InitializationException(MSG_NO_SIZE_SPECIFIED)));
    }

    private static void execute(String fileName, boolean b, String retryFile) throws Exception {
        File file = new File(fileName);

        if (!file.exists()) die("File %s not found!", fileName);
        Scripting.runfile(file, fileName, b, retryFile);
    }

    private static void startAgentTui(String[] args) throws Exception {
        Class<?> tui = Class.forName("net.kimleo.rec.agent.tui.AgentTui");
        tui.getMethod("start", String[].class).invoke(null, (Object) args);
    }
}
