package net.kimleo.rec.loader.strategy;

import net.kimleo.rec.loader.LoadingConfig;
import net.kimleo.rec.loader.RecordLoader;
import net.kimleo.rec.repository.RecRepository;
import net.kimleo.rec.repository.RecordSet;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
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
        if (files == null) {
            die(String.format("Not a directory %s", path));
        }

        Map<String, File> recFiles = Arrays.stream(files)
                .filter(it -> it.getName().endsWith(".rec")).collect(Collectors.toMap(File::getName, it -> it));

        return Arrays.stream(files)
                .filter( file -> recFiles.containsKey(file.getName() + ".rec"))
                .map(f -> new LoadingConfig(f.getAbsolutePath(), recFiles.get(f.getName() + ".rec").getAbsolutePath()))
                .collect(Collectors.toList());
    }

    public static RecRepository repo(String path) {
        List<LoadingConfig> configs = new DefaultLoadingStrategy(path).configs();

        List<RecordSet> sets = configs.stream()
                .map(cfg -> new RecordLoader(cfg).getRecords())
                .collect(Collectors.toList());

        return new RecRepository(sets);
    }

}
