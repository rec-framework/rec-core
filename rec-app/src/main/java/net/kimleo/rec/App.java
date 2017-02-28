package net.kimleo.rec;

import net.kimleo.rec.loader.strategy.DefaultLoadingStrategy;
import net.kimleo.rec.repository.RecRepository;
import net.kimleo.rec.rule.RuleLoader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.nio.file.Files.lines;
import static net.kimleo.rec.scripting.Scripting.runfile;
import static net.kimleo.rec.util.Sys.die;

public class App {
    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            runOverPath(".");
            return;
        }

        switch (args[0]) {
            case "js":
                if (args.length != 2) {
                    die("You need to provide the script file.");
                }

                File file = new File(args[1]);

                if (!file.exists()) {
                    die("File <%s> cannot be found", file.getName());
                }

                runfile(file, file.getName());
                break;
            default:
                if (args.length != 1 || !Files.exists(Paths.get(args[0])))
                    die("You should run with a folder contains rec files!");
                runOverPath(args[0]);
                break;
        }


    }

    private static void runOverPath(String basePath) throws IOException {
        File path = new File(basePath);
        if (path.exists()) {
            RecRepository repo = DefaultLoadingStrategy.repo(basePath);
            Stream<String> lines = lines(Paths.get(basePath, "default.rule"));

            new RuleLoader().load(lines.collect(Collectors.toList())).stream()
                    .map(ruleRunner -> ruleRunner.runOn(repo))
                    .filter(pair -> !pair.getFirst())
                    .forEach(pair -> {
                        pair.getSecond().forEach(it -> System.out.println(it.details()));
                    });
        }
    }
}
