package net.kimleo.rec.loader;

import net.kimleo.rec.repository.DefaultRecConfig;
import net.kimleo.rec.repository.RecConfig;
import net.kimleo.rec.repository.RecordSet;

import java.io.*;
import java.util.List;
import java.util.stream.Collectors;

import static net.kimleo.rec.util.Sys.die;

public class RecordLoader {
    private final File dataFile;
    private final File recFile;

    public RecordLoader(LoadingConfig config) {
        this.dataFile = new File(config.getDataFile());
        this.recFile = new File(config.getRecFile());

        if (! (dataFile.exists() && recFile.exists())) {
            die(String.format("Cannot found given file: <%s> or <%s>", dataFile.getName(), recFile.getName()));
        }
    }

    public RecordSet getRecords() {
        try(BufferedReader dataReader = new BufferedReader(new InputStreamReader(new FileInputStream(dataFile)));
            BufferedReader recReader = new BufferedReader(new InputStreamReader(new FileInputStream(recFile)))) {
            List<String> data = dataReader.lines().collect(Collectors.toList());
            List<String> rec = recReader.lines().collect(Collectors.toList());

            RecConfig recConfig = DefaultRecConfig.Companion.makeTypeFrom(rec);

            return RecordSet.Companion.loadData(data, recConfig);
        } catch (Exception ex) {
            die("Exception found: %s", ex.getMessage());
        }
        return null;
    }
}
