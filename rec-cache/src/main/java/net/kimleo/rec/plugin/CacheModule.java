package net.kimleo.rec.plugin;

import net.kimleo.rec.cache.CachePlugin;
import net.kimleo.rec.model.Tee;
import net.kimleo.rec.model.impl.BufferedCachingTee;
import net.kimleo.rec.model.impl.InMemoryCacheTee;

/**
 * Cache plugin module — zero Rhino dependency.
 * Also implements CachePlugin for Rec's cache() method.
 */
public class CacheModule implements RecPlugin, CachePlugin {

    @Override
    public String name() {
        return "cache";
    }

    @Override
    public Object module() {
        return this;
    }

    @Override
    public Tee cache(int size) {
        return new BufferedCachingTee(size);
    }

    public Tee inMemory(int maxSize) {
        return new InMemoryCacheTee(maxSize);
    }

    public Tee inMemory() {
        return new InMemoryCacheTee();
    }
}
