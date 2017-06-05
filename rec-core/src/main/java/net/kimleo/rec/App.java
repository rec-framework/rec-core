package net.kimleo.rec;

import net.kimleo.rec.exception.InitializationException;
import net.kimleo.rec.logging.Logger;
import net.kimleo.rec.logging.impl.LogManager;
import net.kimleo.rec.v2.scripting.Scripting;
import net.kimleo.rec.v2.utils.Records;

import java.io.File;
import java.util.Arrays;

import static net.kimleo.rec.util.Sys.die;

public class App {

    private final static Logger LOGGER = LogManager.logger("RecApplication");
    public static final String MSG_NO_SIZE_SPECIFIED =
            "Cannot infer binary file column size, please specify one.";

    public static void main(String[] args) throws Exception {
        LOGGER.info("Application started");
        LOGGER.info("Args:" + Arrays.toString(args));
        System.out.println("=> Rec v2");

        if (args.length <= 0) {
            die("You should provide a script file or using commands");
        }

        if (args.length == 1) {
            execute(args[0]);
        }

        if (args.length >= 2) {
            dispatch(args);
        }

        LOGGER.info("Application ended");
    }

    private static void dispatch(String[] args) throws Exception {
        String command = args[0];
        switch (command) {
            case "js":
            case "script":
                execute(args[1]);
                break;
            case "dump":
                String fileName = args[1];
                File binFile = new File(fileName);
                int size;
                if (args.length == 3) {
                    size = Integer.parseInt(args[2]);
                } else {
                    size = inferSizeFromFileName(fileName);
                }
                Records.dump(binFile, size);
                break;
        }
    }

    private static int inferSizeFromFileName(String fileName) {
        return Integer.parseInt(
                Arrays.stream(fileName.split("\\."))
                        .findFirst()
                        .orElseThrow(() ->
                                new InitializationException(MSG_NO_SIZE_SPECIFIED)));
    }

    private static void execute(String fileName) throws Exception {
        File file = new File(fileName);

        if (!file.exists()) die("File %s not found!", fileName);
        Scripting.runfile(file, fileName);
    }
}
