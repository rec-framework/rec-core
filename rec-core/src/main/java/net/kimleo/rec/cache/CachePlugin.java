package net.kimleo.rec.cache;

import net.kimleo.rec.model.Tee;

public interface CachePlugin {
    Tee cache(int size);
}
