package net.kimleo.rec.rule;

import net.kimleo.rec.rule.impl.Exist;
import net.kimleo.rec.rule.impl.Unique;

import java.util.HashMap;
import java.util.Map;

public class RuleRepository {
    private final Map<String, RecRule> rules;

    public RuleRepository(Map<String, RecRule> rules) {
        this.rules = rules;
    }

    public RuleRepository() {
        this(new HashMap<>());
        register("unique", new Unique());
        register("exist", new Exist());
    }

    public void register(String name, RecRule rule) {
        rules.put(name, rule);
    }

    public RecRule rule(String name) {
        return rules.get(name);
    }

}

