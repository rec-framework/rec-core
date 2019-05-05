package net.kimleo.rec.v2;

import net.kimleo.rec.sepval.parser.ParseConfig;
import net.kimleo.rec.v2.model.impl.CSVSource;

import java.io.BufferedReader;
import java.io.Reader;

public final class Rec {

    public static CSVSource csv(String format, char separator, Reader reader) {
        return new CSVSource(reader, format, new ParseConfig(separator));
    }


}
