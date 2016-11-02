package net.kimleo.rec.app

import net.kimleo.rec.loader.RecordLoader
import net.kimleo.rec.loader.strategy.DefaultLoadingStrategy
import net.kimleo.rec.repository.RecRepository

fun main(args: Array<String>) {
    if (args.isEmpty()) {
        val configs = DefaultLoadingStrategy().configs
        val collects = configs.map { RecordLoader(it).getRecords()!! }
        val repo = RecRepository(collects)
    } else {

    }
}