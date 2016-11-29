package net.kimleo.rec.loader

import net.kimleo.rec.repository.DefaultRecConfig
import net.kimleo.rec.repository.RecordSet
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

    fun getRecords(): RecordSet {
        val data = dataFile.readLines()
        val rec = recFile.readLines()

        val type = DefaultRecConfig.makeTypeFrom(rec)
        return RecordSet.loadData(data, type)
    }
}