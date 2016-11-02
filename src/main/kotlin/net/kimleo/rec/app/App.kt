package net.kimleo.rec.app

import net.kimleo.rec.loader.RecordLoader
import net.kimleo.rec.loader.strategy.DefaultLoadingStrategy

fun main(args: Array<String>) {

    if (args.isEmpty()) {
        val configs = DefaultLoadingStrategy().configs
        configs.forEach {
            val recordCollect = RecordLoader(it).getRecords()!!

            recordCollect.forEach(::println)
        }
    } else {

    }
}