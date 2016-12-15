package net.kimleo.rec.util;

import java.util.Formatter;

import static java.lang.System.exit;

public class Sys {
    public static void die(String format, String... args) {
        System.err.println(new Formatter().format(format, (Object[]) args));
        exit(-1);
    }
}
