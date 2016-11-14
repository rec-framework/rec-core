package net.kimleo.rec.loader

import net.kimleo.rec.record.RecCollection
import net.kimleo.rec.record.builder.RecCollectBuilder
import net.kimleo.rec.record.builder.RecTypeBuilder
import java.io.File
import java.io.FileNotFoundException

class RecordLoader(config: LoadingConfig) {
    val dataFile = File(config.dataFile)
    val recFile = File(config.recFile)
    init {
        if (! (dataFile.exists() && recFile.exists()) ) {
            throw FileNotFoundException("Cannot found file ${config.dataFile} or ${config.recFile}")
        }
    }

    fun getRecords(): RecCollection {
        val data = dataFile.readLines()
        val rec = recFile.readLines()

        val type = RecTypeBuilder().build(rec)
        return RecCollectBuilder().build(data, type)
    }
}