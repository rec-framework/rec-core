package net.kimleo.rec.plugin;

import net.kimleo.rec.model.impl.JsonlSource;
import net.kimleo.rec.model.impl.JsonlTarget;

import java.io.File;
import java.io.Reader;

/**
 * JSONL plugin — exposes jsonl(source) and jsonlTarget(file) to JS.
 * Zero Rhino dependency.
 */
public class JsonlModule implements RecPlugin {

    @Override
    public String name() {
        return "jsonl";
    }

    @Override
    public Object module() {
        return this;
    }

    public JsonlSource jsonl(Reader reader) {
        return new JsonlSource(reader);
    }

    public JsonlSource jsonl(File file) {
        return new JsonlSource(file);
    }

    public JsonlTarget jsonlTarget(File file) {
        return new JsonlTarget(file);
    }
}
