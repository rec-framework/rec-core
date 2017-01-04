package net.kimleo.rec.scripting;

import org.junit.Test;
import org.mozilla.javascript.Context;

import java.io.InputStream;
import java.io.InputStreamReader;

public class ScriptingTest {
    @Test
    public void shouldRunJs() throws Exception {
        InputStream stream = this.getClass().getClassLoader().getResourceAsStream("test_intg.js");

        Scripting.runReader("<rec>", Context.enter(), new InputStreamReader(stream));
    }
}
