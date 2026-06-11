package net.kimleo.rec.plugin;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ParquetModuleTest {

    @Test
    public void shouldBeDiscovered() {
        java.util.ServiceLoader<RecPlugin> plugins =
                java.util.ServiceLoader.load(RecPlugin.class);

        boolean found = false;
        for (RecPlugin plugin : plugins) {
            if (plugin instanceof ParquetModule) {
                found = true;
                assertThat(plugin.name(), is("parquet"));
                break;
            }
        }
        assertThat(found, is(true));
    }
}
