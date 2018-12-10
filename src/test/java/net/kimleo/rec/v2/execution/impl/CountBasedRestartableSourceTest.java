package net.kimleo.rec.v2.execution.impl;

import net.kimleo.rec.v2.model.Source;
import org.junit.Test;

import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CountBasedRestartableSourceTest {
    @Test
    public void shouldJustRunAsExpected() throws Exception {
        Stream<Integer> stream = Stream.of(1, 2, 3, 4, 5);
        Source<Integer> restartable = CountBasedRestartableSource.from(stream);

        Optional<Integer> item = restartable.stream().findFirst();
        assertTrue(item.isPresent());
        assertEquals(1, (int) item.get());
}

}