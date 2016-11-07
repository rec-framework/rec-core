package net.kimleo.rec.repository.selector

import net.kimleo.rec.record.RecCollection
import net.kimleo.rec.repository.RecRepository

interface Selector {
    fun findAll(repo: RecRepository): List<RecCollection>

}