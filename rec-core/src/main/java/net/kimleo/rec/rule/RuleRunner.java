package net.kimleo.rec.rule;

import net.kimleo.rec.Pair;
import net.kimleo.rec.repository.RecRepository;
import net.kimleo.rec.repository.selector.Selector;

import java.util.List;

public class RuleRunner {

    private final RecRule rule;
    private final Selector selector;

    public RuleRunner(RecRule rule, Selector selector) {
        this.rule = rule;
        this.selector = selector;
    }

    public Pair<Boolean, List<Result>> runOn(RecRepository repo) {
        return rule.verify(selector.findAll(repo));
    }
}
