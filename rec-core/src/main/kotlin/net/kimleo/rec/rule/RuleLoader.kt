package net.kimleo.rec.rule

import net.kimleo.rec.repository.selector.Selector

class RuleLoader(val ruleRepository: RuleRepository = RuleRepository()) {
    fun load(lines: List<String>): List<RuleRunner> {
        return lines.map { line ->
            val split = line.split(":")
            val rule = split[0].trim()
            val selector = split[1].trim()

            RuleRunner(ruleRepository.rule(rule), Selector.of(selector))
        }
    }
}

