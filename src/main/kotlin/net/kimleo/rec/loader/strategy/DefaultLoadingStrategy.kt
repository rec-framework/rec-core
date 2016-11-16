package net.kimleo.rec.loader.strategy

import net.kimleo.rec.loader.LoadingConfig
import java.io.File
import java.io.FileNotFoundException

class DefaultLoadingStrategy: LoadingStrategy {
    override val configs: List<LoadingConfig>
        get() {
            val lists = File(".").list()
            val recs = lists.filter({ it.endsWith(".rec") })

            recs.forEach {
                if (!lists.contains(it.dropLast(4))) {
                    throw FileNotFoundException("No data file found for $it")
                }
            }

            return recs.map { it.dropLast(4) }.zip(recs, ::LoadingConfig)
        }
}