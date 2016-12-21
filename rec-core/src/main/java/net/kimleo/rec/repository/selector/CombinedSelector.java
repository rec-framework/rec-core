package net.kimleo.rec.repository.selector;

import net.kimleo.rec.repository.RecRepository;
import net.kimleo.rec.repository.RecordSet;

import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class CombinedSelector implements Selector {
    private final List<Selector> selectors;

    public CombinedSelector(List<Selector> selectors) {
        this.selectors = selectors;
    }

    @Override
    public List<RecordSet> findAll(RecRepository repo) {
        return selectors.stream().reduce(
                Stream.<RecordSet>empty(),
                (stm, selector) -> Stream.concat(stm, selector.findAll(repo).stream()),
                Stream::concat).collect(toList());
    }
}
