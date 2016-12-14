package net.kimleo.rec.sepval.parser;

public class ParseConfig {
    public final char delimiter;
    public final char escape;

    public ParseConfig(char delimiter, char escape) {
        this.delimiter = delimiter;
        this.escape = escape;
    }

    public ParseConfig(char delimiter) {
        this(delimiter, '"');
    }

    public ParseConfig() {
        this(',', '"');
    }
}
