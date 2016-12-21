package net.kimleo.rec.repository;

import net.kimleo.rec.accessor.Accessor;
import net.kimleo.rec.sepval.parser.ParseConfig;

public interface RecConfig {
    String name();
    ParseConfig parseConfig();
    String key();
    String format();
    Accessor<String> accessor();
    boolean keepOrigin();
}
