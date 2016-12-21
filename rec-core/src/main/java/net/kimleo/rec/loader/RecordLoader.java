package net.kimleo.rec.loader;

import net.kimleo.rec.repository.DefaultRecConfig;
import net.kimleo.rec.repository.RecConfig;
import net.kimleo.rec.repository.RecordSet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.kimleo.rec.util.Sys.die;

public class RecordLoader {
    private final File dataFile;
    private final File recFile;

    public RecordLoader(LoadingConfig config) {
        this.dataFile = new File(config.dataFile);
        this.recFile = new File(config.recFile);

        if (! (dataFile.exists() && recFile.exists())) {
            die(String.format("Cannot found given file: <%s> or <%s>", dataFile.getName(), recFile.getName()));
        }
    }

    public RecordSet getRecords() {
        try(BufferedReader dataReader = new BufferedReader(new InputStreamReader(new FileInputStream(dataFile)));
            BufferedReader recReader = new BufferedReader(new InputStreamReader(new FileInputStream(recFile)))) {
            Stream<String> data = dataReader.lines();
            List<String> rec = recReader.lines().collect(Collectors.toList());

            RecConfig recConfig = DefaultRecConfig.makeTypeFrom(rec);

            return RecordSet.loadData(data, recConfig);
        } catch (Exception ex) {
            die("Exception found: %s", ex.getMessage());
        }
        return null;
    }
}
