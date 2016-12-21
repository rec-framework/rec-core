package net.kimleo.rec.repository.selector;

import net.kimleo.rec.repository.RecRepository;
import net.kimleo.rec.repository.RecordSet;

import java.util.List;

final public class AliasSelector implements Selector {
    public final FieldSelector selector;
    public final String alias;

    public AliasSelector(FieldSelector selector, String alias) {
        this.selector = selector;
        this.alias = alias;
    }

    @Override
    public List<RecordSet> findAll(RecRepository repo) {
        selector.setNewType(alias);
        return selector.findAll(repo);
    }
}
