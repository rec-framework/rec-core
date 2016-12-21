package net.kimleo.rec.repository;

import net.kimleo.rec.accessor.Accessor;
import net.kimleo.rec.sepval.parser.ParseConfig;
import net.kimleo.rec.sepval.parser.SimpleParser;

import java.util.HashMap;
import java.util.List;

public class DefaultRecConfig implements RecConfig {
    /*
    package net.kimleo.rec.repository

import net.kimleo.rec.accessor.Accessor
import net.kimleo.rec.orElse
import net.kimleo.rec.sepval.parser.ParseConfig
import net.kimleo.rec.sepval.parser.SimpleParser

class DefaultRecConfig(
        override var name: String,
        override var format: String,
        override var parseConfig: ParseConfig = ParseConfig(),
        override var key: String? = null,
        override var accessor: Accessor<String> = Accessor(SimpleParser().parse(format).values.toTypedArray())) : RecConfig {
    companion object {
        fun create(name: String, format: String): RecConfig {
            return DefaultRecConfig(name, format)
        }

        fun makeTypeFrom(lines: List<String>): RecConfig {
            val name = lines.first().trim()
            val configs = hashMapOf<String, String>()
            lines.drop(1).forEach {
                val (key, value) = it.split("=")
                configs.put(key.trim(), value.trim())
            }

            val format = configs["format"]!!
            val config = ParseConfig(
                    configs["delimiter"]?.first().orElse { ',' },
                    configs["escape"]?.first().orElse { '"' })

            return DefaultRecConfig(name, format, config, configs["key"], Accessor(SimpleParser().parse(format).values.toTypedArray()))
        }
    }
}
     */

    private final String name;
    private final String format;
    private final ParseConfig parseConfig;
    private final String key;
    private final Accessor<String> accessor;

    DefaultRecConfig(String name, String format, ParseConfig parseConfig, String key, Accessor<String> accessor) {
        this.name = name;
        this.format = format;
        this.parseConfig = parseConfig;
        this.key = key;
        this.accessor = accessor;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public ParseConfig parseConfig() {
        return parseConfig;
    }

    @Override
    public String key() {
        return key;
    }

    @Override
    public String format() {
        return format;
    }

    @Override
    public Accessor<String> accessor() {
        return accessor;
    }

    public static RecConfig create(String name, String format) {
        return new DefaultRecConfig(
                name, format, new ParseConfig(), null,
                accessorFrom(format));
    }

    public static RecConfig makeTypeFrom(List<String> lines) {
        /*
        val name = lines.first().trim()
            val configs = hashMapOf<String, String>()
            lines.drop(1).forEach {
                val (key, value) = it.split("=")
                configs.put(key.trim(), value.trim())
            }

            val format = configs["format"]!!
            val config = ParseConfig(
                    configs["delimiter"]?.first().orElse { ',' },
                    configs["escape"]?.first().orElse { '"' })

            return DefaultRecConfig(name, format, config, configs["key"], Accessor(SimpleParser().parse(format).values.toTypedArray()))
         */

        String name = lines.get(0).trim();

        HashMap<String, String> configs = new HashMap<>();
        lines.stream().skip(1).forEach(line -> {
            String[] pair = line.split("=");
            String key = pair[0];
            String value = pair[1];

            configs.put(key, value);
        });

        String format = configs.get("format");
        ParseConfig config = new ParseConfig(
                configs.getOrDefault("delimiter", ",").charAt(0),
                configs.getOrDefault("escape", "\"").charAt(0));

        return new DefaultRecConfig(name, format, config, configs.get("key"), accessorFrom(format));
    }

    private static Accessor<String> accessorFrom(String format) {
        return new Accessor<>(new SimpleParser().parse(format).getValues().toArray(new String[]{}));
    }
}
