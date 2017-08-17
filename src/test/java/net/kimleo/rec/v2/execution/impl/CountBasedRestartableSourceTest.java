package net.kimleo.rec.v2.execution.impl;

import net.kimleo.rec.v2.model.Source;
import net.kimleo.rec.v2.scripting.Scripting;
import net.kimleo.rec.v2.scripting.module.Rec;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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
        Scripting.runfile(new File("src/test/resources/restartability.js"),
                "restartability.js");
    }

    @Test
    public void shouldGenerateRetryFile() throws Exception {
        Rec.setExecutionContext(CountBasedExecutionContext.initialContext());
        Files.list(Paths.get(".")).filter(path -> path.toString().endsWith(".retry")).forEach(deleteFile());
        try {
            Scripting.runfile(new File("src/test/resources/restartability.js"),
                    "restartability.js");
            fail();
        } catch (Exception ex) {
            assertTrue(ex instanceof RuntimeException);
        }

        assertTrue(Files.find(Paths.get("."), 1,
                        (file, attr) -> file.toString().endsWith(".retry"))
                .count() == 1);
    }

    private Consumer<Path> deleteFile() {
        return (path) -> {
            try {
                Files.deleteIfExists(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
    }
}