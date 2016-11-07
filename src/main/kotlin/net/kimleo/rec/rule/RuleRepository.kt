package net.kimleo.rec.rule

import net.kimleo.rec.orElse
import net.kimleo.rec.rule.impl.Exist
import net.kimleo.rec.rule.impl.Unique
import java.util.*

class RuleRepository(val rules: HashMap<String, RecRule> = hashMapOf()) {

    init {
        register("unique", Unique())
        register("exist", Exist())
    }

    fun register(name: String, rule: RecRule) {
        rules[name] = rule
    }

    fun rule(name: String): RecRule {
        return rules[name].orElse { throw RuntimeException("Cannot found rule $name") }
    }
}