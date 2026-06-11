package net.kimleo.rec.plugin;

import net.kimleo.rec.model.impl.ParquetSource;
import net.kimleo.rec.model.impl.ParquetTarget;

import java.io.File;

/**
 * Parquet plugin — exposes parquet(file) and parquetTarget(file) to JS.
 * Zero Rhino dependency.
 */
public class ParquetModule implements RecPlugin {

    @Override
    public String name() {
        return "parquet";
    }

    @Override
    public Object module() {
        return this;
    }

    public ParquetSource parquet(File file) {
        return new ParquetSource(file);
    }

    public ParquetTarget parquetTarget(File file) {
        return new ParquetTarget(file);
    }
}
