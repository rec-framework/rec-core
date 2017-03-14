package net.kimleo.rec.v2.utils;

import org.junit.Test;

import java.io.File;
import java.net.URI;

public class RecordsTest {
    @Test
    public void dump() throws Exception {

        URI uri = this.getClass().getClassLoader().getResource("caching.bin.test").toURI();

        Records.dump(new File(uri), 3);
    }

}