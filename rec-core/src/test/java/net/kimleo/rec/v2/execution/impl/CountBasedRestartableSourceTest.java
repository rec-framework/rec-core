package net.kimleo.rec.v2.execution.impl;

import net.kimleo.rec.v2.model.Source;
import net.kimleo.rec.v2.scripting.Scripting;
import net.kimleo.rec.v2.scripting.module.Rec;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.util.stream.Stream;

public class CountBasedRestartableSourceTest {
    @Test
    public void shouldJustRunAsExpected() throws Exception {
        Stream<Integer> stream = Stream.of(1, 2, 3, 4, 5);
        Source<Integer> restartable = CountBasedRestartableSource.from(stream);

        restartable.stream().findFirst().get().equals(1);
    }

    @Test
    public void shouldReturnLastRun() throws Exception {
        Stream<Integer> stream = Stream.of(1, 2, 3, 4, 5);

        Source<Integer> restartable =
                CountBasedRestartableSource.from(stream, new CountBasedExecutionContext(2));

        restartable.stream().findFirst().get().equals(3);
    }

    @Test
    public void restartabilityTest() throws Exception {
        Rec.setExecutionContext(new CountBasedExecutionContext(4));
        Scripting.runfile(new File("src/test/resources/restartability.js"), "restartability.js");
    }
}