package net.kimleo.rec.rule

import net.kimleo.rec.record.RecCollection

interface RecRule {
    fun verify(rec: RecCollection): Pair<Boolean, List<Result>>
}