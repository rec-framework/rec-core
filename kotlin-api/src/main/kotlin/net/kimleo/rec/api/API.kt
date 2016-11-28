package net.kimleo.rec.api

import net.kimleo.rec.loader.strategy.DefaultLoadingStrategy
import net.kimleo.rec.record.Record
import net.kimleo.rec.repository.RecRepository

private var globalName: RecRepository? = null

fun load(path: String): RecRepository =
        DefaultLoadingStrategy.repo(path)
