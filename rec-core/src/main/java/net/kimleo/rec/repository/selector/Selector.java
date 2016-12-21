package net.kimleo.rec.repository.selector;

import net.kimleo.rec.repository.RecRepository;
import net.kimleo.rec.repository.RecordSet;
import net.kimleo.rec.repository.selector.expr.SelectorParser;

import java.util.List;

public interface Selector {
    List<RecordSet> findAll(RecRepository repo);


    static Selector of(String expr) {
        List<Selector> selectors = SelectorParser.INSTANCE.parse(SelectorParser.INSTANCE.lex(expr));
        return new CombinedSelector(selectors);
    }
}