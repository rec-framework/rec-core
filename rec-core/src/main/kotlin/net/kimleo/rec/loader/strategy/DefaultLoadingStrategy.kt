package net.kimleo.rec.loader.strategy

import net.kimleo.rec.loader.LoadingConfig
import net.kimleo.rec.loader.RecordLoader
import net.kimleo.rec.repository.RecRepository
import java.io.File
import java.io.FileNotFoundException
import java.nio.file.Files
import java.nio.file.Paths

class DefaultLoadingStrategy(val path: String) : LoadingStrategy {
    override val configs: List<LoadingConfig>
        get() {
            val lists = File(path).list()
            val recs = lists.filter({ it.endsWith(".rec") }).map {
                Paths.get(path, it).toString()
            }

            recs.forEach {
                if (!Files.exists(Paths.get(it.dropLast(4)))) {
                    throw FileNotFoundException("No data file found for $it")
                }
            }

            return recs.map { it.dropLast(4) }.zip(recs, ::LoadingConfig)
        }

    companion object {
        fun repo(path: String): RecRepository {
            val configs = DefaultLoadingStrategy(path).configs
            val collects = configs.map { RecordLoader(it).getRecords() }
            return RecRepository(collects)
        }
    }
}