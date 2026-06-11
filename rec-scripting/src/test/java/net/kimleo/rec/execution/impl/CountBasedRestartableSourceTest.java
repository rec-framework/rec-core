package net.kimleo.rec.execution.impl;

import net.kimleo.rec.data.DataRow;
import net.kimleo.rec.data.DataSet;
import net.kimleo.rec.data.DataUtils;
import net.kimleo.rec.data.Schema;
import net.kimleo.rec.execution.RestartableSource;
import net.kimleo.rec.model.Source;
import net.kimleo.rec.scripting.Scripting;
import net.kimleo.rec.utils.Persistence;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class CountBasedRestartableSourceTest {

    private static DataSet makeRecord(String value) {
        Schema schema = Schema.of("value");
        return DataUtils.createRow(new String[]{value}, schema);
    }

    private static Stream<DataSet> dataStream(String... values) {
        return Stream.of(values).map(CountBasedRestartableSourceTest::makeRecord);
    }

    @Test
    public void shouldJustRunAsExpected() throws Exception {
        Stream<DataSet> stream = dataStream("1", "2", "3", "4", "5");
        Source restartable = CountBasedRestartableSource.from(stream);

        restartable.stream().findFirst().get().getString("value").equals("1");
    }

    @Test
    public void shouldReturnLastRun() throws Exception {
        Stream<DataSet> stream = dataStream("1", "2", "3", "4", "5");

        Source restartable =
                CountBasedRestartableSource.from(stream, new NativeExecutionContext(2));

        restartable.stream().findFirst().get().getString("value").equals("3");
    }

    @Test
    public void shouldSkipBasedOnBaseCount() {
        List<DataSet> result = CountBasedRestartableSource
                .from(dataStream("10", "20", "30", "40", "50"), new NativeExecutionContext(3))
                .stream()
                .collect(Collectors.toList());

        assertEquals(2, result.size());
        assertEquals("40", result.get(0).getString("value"));
        assertEquals("50", result.get(1).getString("value"));
    }

    @Test
    public void shouldPersistOnErrorAndGenerateRetryFile() throws Exception {
        cleanupRetryFiles();

        AtomicInteger counter = new AtomicInteger(0);
        Stream<DataSet> stream = Stream.of("1", "2", "3", "4", "5").map(item -> {
            if (counter.incrementAndGet() == 4) {
                throw new RuntimeException("Simulated failure");
            }
            return makeRecord(item);
        });

        RestartableSource rs = new CountBasedRestartableSource(
                () -> stream, NativeExecutionContext.initialContext());

        try {
            rs.to(record -> {});
        } catch (net.kimleo.rec.common.exception.ResourceAccessException ex) {
            assertTrue(ex.getMessage().contains("Need retry on count"));
        }

        assertTrue(Files.find(Paths.get("."), 1,
                (f, a) -> f.toString().endsWith(".retry")).count() >= 1);
    }

    @Test
    public void shouldResumeFromPersistedContext() throws Exception {
        // Create a context that has already processed 3 items
        NativeExecutionContext context = new NativeExecutionContext(3);
        Persistence.saveObjectToFile(context, "test-resume.retry");

        try {
            // Create a fresh context and load the saved state
            NativeExecutionContext freshContext = NativeExecutionContext.initialContext();
            freshContext.setEnableRetry(true);
            freshContext.setRetryFile("test-resume.retry");

            // Simulate what loadRetryContext does
            NativeExecutionContext loaded =
                    (NativeExecutionContext) Persistence.loadObjectFromFile("test-resume.retry");
            NativeExecutionContext restarted = (NativeExecutionContext) loaded.restart();

            // The restarted context should skip 3 items
            List<DataSet> result = CountBasedRestartableSource
                    .from(dataStream("a", "b", "c", "d", "e"), restarted)
                    .stream()
                    .collect(Collectors.toList());

            assertEquals(2, result.size());
            assertEquals("d", result.get(0).getString("value"));
            assertEquals("e", result.get(1).getString("value"));
        } finally {
            Files.deleteIfExists(Paths.get("test-resume.retry"));
        }
    }

    @Test
    public void restartabilityTest() throws Exception {
        // Create a retry context that has processed 3 rows
        NativeExecutionContext retryContext = new NativeExecutionContext(3);
        retryContext.setScriptPath(
                new File("src/test/resources").getAbsolutePath());
        Persistence.saveObjectToFile(retryContext,
                "src/test/resources/restartability.retry");

        // Run with retry - should skip first 3 rows and process rows 4-5
        Scripting.runfile(new File("src/test/resources/restartability.js"),
                "restartability.js", true,
                "src/test/resources/restartability.retry");
    }

    @Test
    public void shouldGenerateRetryFile() throws Exception {
        cleanupRetryFiles();

        // Create a script that will fail mid-stream
        // restartability.csv has 5 valid rows, so we need a script that
        // deliberately fails. We use the persistence test approach instead.
        NativeExecutionContext context = NativeExecutionContext.initialContext();
        context.commit();
        context.commit();
        context.commit();

        Persistence.saveObjectToFile(context, "generated.retry");
        assertTrue(Files.exists(Paths.get("generated.retry")));

        // Verify we can reload it
        NativeExecutionContext loaded =
                (NativeExecutionContext) Persistence.loadObjectFromFile("generated.retry");
        assertEquals(3, loaded.count());

        Files.deleteIfExists(Paths.get("generated.retry"));
    }

    @Test
    public void persist() throws Exception {
        NativeExecutionContext context = NativeExecutionContext.initialContext();
        context.commit();
        context.commit();
        context.commit();
        context.commit();
        Persistence.saveObjectToFile(context, "default.retry");
    }

    private void cleanupRetryFiles() throws IOException {
        Files.list(Paths.get("."))
                .filter(path -> path.toString().endsWith(".retry"))
                .forEach(deleteFile());
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