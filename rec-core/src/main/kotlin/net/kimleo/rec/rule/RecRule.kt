package net.kimleo.rec.rule

import net.kimleo.rec.repository.RecordSet

interface RecRule {
    fun verify(recs: List<RecordSet>): Pair<Boolean, List<Result>>
}