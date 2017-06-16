package net.kimleo.rec.v2.utils;

import net.kimleo.rec.v2.execution.impl.CountBasedExecutionContext;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

public class PersistenceTest {

    @Test
    public void shouldPersistObject() throws Exception {
        Files.deleteIfExists(Paths.get("file.out"));
        CountBasedExecutionContext context = new CountBasedExecutionContext(15);
        context.commit();

        Persistence.saveObjectToFile(context, "file.out");

        CountBasedExecutionContext loadedContext =
                (CountBasedExecutionContext)Persistence.loadObjectFromFile("file.out");

        assertThat(loadedContext.count(), is(1));
        assertThat(loadedContext.baseCount(), is(15));
        assertFalse(loadedContext.ready());

        Files.delete(Paths.get("file.out"));
    }
}