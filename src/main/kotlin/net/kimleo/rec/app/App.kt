package net.kimleo.rec.app

import net.kimleo.rec.loader.RecordLoader
import net.kimleo.rec.loader.strategy.DefaultLoadingStrategy
import net.kimleo.rec.repository.RecRepository
import net.kimleo.rec.rule.RuleLoader
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader

fun main(args: Array<String>) {
    if (args.isEmpty()) {
        val configs = DefaultLoadingStrategy().configs
        val collects = configs.map { RecordLoader(it).getRecords()!! }
        val repo = RecRepository(collects)

        val rules = lines("./default.rule")
        RuleLoader().load(rules).map {
            it.runOn(repo)
        }.filter {
            !it.first
        }.forEach {
            it.second.forEach {
                println(it.details)
            }
        }
    }
}


fun lines(file: String): List<String> {
    val reader = BufferedReader(InputStreamReader(FileInputStream(File(file))))

    val lines = reader.readLines()
    reader.close()
    return lines
}