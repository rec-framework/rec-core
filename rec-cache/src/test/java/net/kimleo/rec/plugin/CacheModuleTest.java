package net.kimleo.rec.plugin;

import net.kimleo.rec.model.Tee;
import net.kimleo.rec.model.impl.BufferedCachingTee;
import net.kimleo.rec.model.impl.InMemoryCacheTee;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class CacheModuleTest {

    @Test
    public void shouldBeDiscovered() {
        java.util.ServiceLoader<RecPlugin> plugins =
                java.util.ServiceLoader.load(RecPlugin.class);

        boolean found = false;
        for (RecPlugin plugin : plugins) {
            if (plugin instanceof CacheModule) {
                found = true;
                assertThat(plugin.name(), is("cache"));
                break;
            }
        }
        assertThat(found, is(true));
    }

    @Test
    public void shouldCreateBufferedCache() {
        CacheModule module = new CacheModule();
        Tee tee = module.cache(1024);
        assertThat(tee, instanceOf(BufferedCachingTee.class));
    }

    @Test
    public void shouldCreateInMemoryCache() {
        CacheModule module = new CacheModule();
        Tee tee = module.inMemory(100);
        assertThat(tee, instanceOf(InMemoryCacheTee.class));
    }

    @Test
    public void shouldCreateUnlimitedInMemoryCache() {
        CacheModule module = new CacheModule();
        Tee tee = module.inMemory();
        assertThat(tee, instanceOf(InMemoryCacheTee.class));
    }
}