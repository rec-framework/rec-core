package net.kimleo.rec.v2.execution.impl;

import net.kimleo.rec.v2.model.Source;
import org.junit.Test;

import java.util.stream.Stream;

public class CountBasedRestartableSourceTest {
    @Test
    public void shouldJustRunAsExpected() throws Exception {
        Stream<Integer> stream = Stream.of(1, 2, 3, 4, 5);
        Source<Integer> restartable = CountBasedRestartableSource.from(stream);

        restartable.stream().findFirst().ifPresent(it -> it.equals(1));
    }

    @Test
    public void shouldReturnLastRun() throws Exception {
        Stream<Integer> stream = Stream.of(1, 2, 3, 4, 5);

        Source<Integer> restartable = CountBasedRestartableSource.from(stream, new CountBasedExecutionContext(2));

        restartable.stream().findFirst().ifPresent(it -> it.equals(1));
    }
}