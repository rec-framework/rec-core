package net.kimleo.rec.rule

import net.kimleo.rec.repository.RecRepository
import net.kimleo.rec.repository.selector.Selector

class RuleRunner(val rule: RecRule, val selector: Selector) {
    fun runOn(repo: RecRepository): Pair<Boolean, List<Result>> {
        return rule.verify(selector.findAll(repo))
    }
}