package net.kimleo.rec.sepval;

public class ParseConfig {
    public final char delimiter;
    public final char escape;

    public static final ParseConfig DEFAULT = new ParseConfig();
    public static final ParseConfig BY_PIPE = new ParseConfig('|');

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
