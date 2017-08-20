package net.kimleo.rec.v2.model.impl;

import net.kimleo.rec.v2.scripting.Scripting;
import org.junit.Test;

import java.io.File;

public class ReactiveTeeTest {
    @Test
    public void test() throws Exception {
        Scripting.runfile(new File("src/test/resources/reactive.js"), "reactive.js", false, "");
    }

}