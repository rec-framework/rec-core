package net.kimleo.rec.v2.utils;

import net.kimleo.rec.v2.execution.impl.NativeExecutionContext;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class PersistenceTest {

    @Test
    public void shouldPersistObject() throws Exception {
        Files.deleteIfExists(Paths.get("file.out"));
        NativeExecutionContext context = new NativeExecutionContext(15);
        context.commit();

        Persistence.saveObjectToFile(context, "file.out");

        NativeExecutionContext loadedContext =
                (NativeExecutionContext)Persistence.loadObjectFromFile("file.out");

        assertThat(loadedContext.count(), is(1));
        assertThat(loadedContext.state(), is(15));

        Files.delete(Paths.get("file.out"));
    }
}