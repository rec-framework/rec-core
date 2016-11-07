package net.kimleo.rec.rule

import net.kimleo.rec.repository.RecRepository
import java.util.*

class RuleFactory(val rules: HashMap<String, RecRule> = hashMapOf(),
                  val relations: HashMap<String, RecRelation> = hashMapOf()) {
    fun createRule(name: String): RecRule? {
        return rules[name]
    }

    fun register(name: String, rule: RecRule) {
        rules[name] = rule
    }

    fun register(name: String, relation: RecRelation) {
        relations[name] = relation
    }
}