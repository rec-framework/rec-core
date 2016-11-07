package net.kimleo.rec.rule

import net.kimleo.rec.repository.RecRepository

class UniqueRule(val rec: String, val fields: List<String>): RecRule {
    override fun verify(repo: RecRepository): Pair<Boolean, List<Result>> {
        val unique = repo.from(rec)
                .select(*fields.toTypedArray())
                .isUnique()
        val results = arrayListOf<Result>()
        if (!unique) {
            val collect = repo.from(rec)
            collect.select(*fields.toTypedArray()).groupBy({ it }).filter { e -> e.value.size > 1 }.forEach { rec, recs ->
                recs.forEach {
                    results.add(object: Result {
                        override val details = "duplicate record found with keys($fields): ${it.text}"
                    })
                }
            }
        }
        return Pair(unique, results)
    }
}