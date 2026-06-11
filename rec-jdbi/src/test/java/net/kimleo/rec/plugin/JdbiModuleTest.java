package net.kimleo.rec.plugin;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class JdbiModuleTest {

    @Test
    public void shouldBeDiscovered() {
        java.util.ServiceLoader<RecPlugin> plugins =
                java.util.ServiceLoader.load(RecPlugin.class);

        boolean found = false;
        for (RecPlugin plugin : plugins) {
            if (plugin instanceof JdbiModule) {
                found = true;
                assertThat(plugin.name(), is("jdbi"));
                break;
            }
        }
        assertThat(found, is(true));
    }
}
