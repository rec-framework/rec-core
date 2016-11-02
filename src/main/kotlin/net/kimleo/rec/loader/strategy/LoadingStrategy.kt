package net.kimleo.rec.loader.strategy

import net.kimleo.rec.loader.LoadingConfig

interface LoadingStrategy {
    val configs: List<LoadingConfig>
}