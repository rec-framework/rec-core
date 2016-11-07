package net.kimleo.rec.rule

import net.kimleo.rec.repository.RecRepository

interface RecRule {
    fun verify(repo: RecRepository): Pair<Boolean, List<Result>>
}