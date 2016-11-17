package net.kimleo.rec.rule

import net.kimleo.rec.repository.RecCollection

interface RecRule {
    fun verify(recs: List<RecCollection>): Pair<Boolean, List<Result>>
}