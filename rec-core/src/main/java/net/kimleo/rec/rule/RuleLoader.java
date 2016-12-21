package net.kimleo.rec.rule;

import net.kimleo.rec.repository.selector.Selector;

import java.util.List;
import java.util.stream.Collectors;

public class RuleLoader {

    public final RuleRepository repository = new RuleRepository();

    public List<RuleRunner> load(List<String> lines) {
        return lines.stream().map(line -> {
            String[] pair = line.split(":");
            String rule = pair[0];
            String selector = pair[1];

            return new RuleRunner(repository.rule(rule), Selector.of(selector));
        }).collect(Collectors.toList());
    }
}
