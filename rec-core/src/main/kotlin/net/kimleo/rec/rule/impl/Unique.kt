package net.kimleo.rec.rule.impl

import net.kimleo.rec.repository.RecordSet
import net.kimleo.rec.rule.RecRule
import net.kimleo.rec.rule.Result

class Unique : RecRule {
    override fun verify(recs: List<RecordSet>): Pair<Boolean, List<Result>> {
        assert(recs.size == 1)
        val rec = recs.first()
        val unique = rec.isUnique()
        val results = arrayListOf<Result>()
        if (!unique) {
            val collect = rec
            collect.groupBy({ it }).filter { e -> e.value.size > 1 }.forEach { entry ->
                entry.value.forEach {
                    results.add(object : Result {
                        override val details = "Duplicate `${collect.config.format}` in record: [${it.parent().text}]"
                    })
                }
            }
        }
        return Pair(unique, results)
    }
}