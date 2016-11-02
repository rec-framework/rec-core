package net.kimleo.rec.loader

import net.kimleo.rec.record.RecordCollection
import net.kimleo.rec.record.builder.RecordCollectionBuilder
import net.kimleo.rec.record.builder.RecordTypeBuilder
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

    fun getRecords(): RecordCollection? {
        val data = dataFile.readLines()
        val rec = recFile.readLines()

        val type = RecordTypeBuilder().build(rec)
        return RecordCollectionBuilder().build(data, type)
    }
}