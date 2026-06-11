package net.kimleo.rec.scripting;

import org.junit.Test;

import java.io.File;

public class ScriptingTest {
    @Test
    public void test() throws Exception {
        Scripting.runfile(new File("src/test/resources/test.js"), "test.js", false, "");
    }
}