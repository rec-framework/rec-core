package net.kimleo.rec.sepval.parser;

import net.kimleo.rec.sepval.SepValEntry;

import java.util.ArrayList;

public class SimpleParser {
    private final ParseConfig config;

    public SimpleParser(ParseConfig config) {
        this.config = config;
    }

    public SimpleParser() {
        this(new ParseConfig());
    }

    public SepValEntry parse(String input) {
        ParseState state = new ParseState(input);
        ArrayList<String> fields = new ArrayList<>();
        while (!state.eof()) {
            fields.add(parseField(state));
        }

        return new SepValEntry(fields, input);
    }

    private String parseField(ParseState state) {
        StringBuilder builder = new StringBuilder();
        Character c = state.current();
        while (c != null && c != config.delimiter) {
            if (builder.length() == 0) {
                if (isSpace(c)) {
                    c = state.next();
                    continue;
                }
                if (c == config.escape) {
                    return parseEscaped(state);
                }
            }
            builder.append(c);
            c = state.next();
        }
        state.next();
        return builder.toString().trim();
    }

    private String parseEscaped(ParseState state) {

        StringBuilder builder = new StringBuilder();
        assert (state.current() == config.escape);

        Character c = state.next();
        while (!state.eof() || c != config.delimiter) {
            if (c == config.escape) {
                c = state.next();
                if (c == null || c != config.escape) {
                    expectDelimiter(state);
                    state.next();
                    break;
                }
            }
            builder.append(c);
            c = state.next();
        }
        return builder.toString();
    }

    private void expectDelimiter(ParseState state) {
        while (isSpace(state.current())) {
            state.next();
        }

        assert((state.current() == null || state.current() == config.delimiter));
    }

    private boolean isSpace(Character c) {
        return c != null && (c == ' ' || c == '\t');
    }
}
