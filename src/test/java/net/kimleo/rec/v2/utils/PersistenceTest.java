package net.kimleo.rec.v2.utils;

import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class PersistenceTest {

    @Test
    public void shouldPersistObject() throws Exception {
        Files.deleteIfExists(Paths.get("file.out"));
        PersistableClass persistableClass = new PersistableClass("kimmy", "leo");

        Persistence.saveObjectToFile(persistableClass, "file.out");

        PersistableClass loadedPersistableClass = Persistence.loadObjectFromFile("file.out");

        assertThat(loadedPersistableClass.getName(), is("kimmy"));
        assertThat(loadedPersistableClass.getPassword(), is("leo"));

        Files.delete(Paths.get("file.out"));
    }
}