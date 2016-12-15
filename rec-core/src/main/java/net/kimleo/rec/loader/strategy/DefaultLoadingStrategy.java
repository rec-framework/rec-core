package net.kimleo.rec.loader.strategy;

import net.kimleo.rec.loader.LoadingConfig;
import net.kimleo.rec.loader.RecordLoader;
import net.kimleo.rec.repository.RecRepository;
import net.kimleo.rec.repository.RecordSet;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static net.kimleo.rec.util.Sys.die;

public class DefaultLoadingStrategy implements LoadingStrategy {

    private final String path;

    public DefaultLoadingStrategy(String path) {
        this.path = path;
    }

    @Override
    public List<LoadingConfig> configs() {
        File[] files = new File(path).listFiles();
        List<File> recs = Arrays.stream(files)
                .filter(file -> file.getName().endsWith(".rec")).collect(Collectors.toList());

        recs.forEach(file -> {
            if (Files.exists(Paths.get(dataFileOf(file.getName())))) {
                die(String.format("No data file fount for %s", file));
            }
        });

        return recs.stream()
                .map(file -> new LoadingConfig(dataFileOf(file.getAbsolutePath()), file.getAbsolutePath()))
                .collect(Collectors.toList());
    }

    public static RecRepository repo(String path) {
        List<LoadingConfig> configs = new DefaultLoadingStrategy(path).configs();

        List<RecordSet> sets = configs.stream()
                .map(cfg -> new RecordLoader(cfg).getRecords())
                .collect(Collectors.toList());

        return new RecRepository(sets);
    }

    @NotNull
    private String dataFileOf(String file) {
        return file.substring(0, file.length() - 4);
    }
}
