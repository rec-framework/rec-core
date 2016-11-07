package net.kimleo.rec.rule.impl

import net.kimleo.rec.record.RecCollection
import net.kimleo.rec.rule.RecRule
import net.kimleo.rec.rule.Result

class Unique : RecRule {
    override fun verify(rec: RecCollection): Pair<Boolean, List<Result>> {
        val unique = rec.isUnique()
        val results = arrayListOf<Result>()
        if (!unique) {
            val collect = rec
            collect.groupBy({ it }).filter { e -> e.value.size > 1 }.forEach { rec, recs ->
                recs.forEach {
                    results.add(object : Result {
                        override val details = "duplicate record found with ${collect.type.format}: ${it.text}"
                    })
                }
            }
        }
        return Pair(unique, results)
    }
}