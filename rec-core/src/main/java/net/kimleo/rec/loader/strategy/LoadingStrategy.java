package net.kimleo.rec.loader.strategy;

import net.kimleo.rec.loader.LoadingConfig;

import java.util.List;

public interface LoadingStrategy {
    List<LoadingConfig> configs();
}
