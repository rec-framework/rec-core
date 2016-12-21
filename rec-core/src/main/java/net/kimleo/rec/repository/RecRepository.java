package net.kimleo.rec.repository;

import net.kimleo.rec.Pair;
import net.kimleo.rec.repository.selector.Selector;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

public class RecRepository implements Iterable<RecordSet> {

    private final List<RecordSet> collections;

    private final Map<String, RecordSet> repo;

    public int getSize() {
        return collections.size();
    }

    public RecRepository(List<RecordSet> collections) {
        this.collections = collections;
        repo = toRepoMap(collections);
    }

    private Map<String, RecordSet> toRepoMap(List<RecordSet> collections) {
        return collections
                .stream()
                .map(collect -> new Pair<>(collect.getConfig().name(), collect))
                .collect(Collectors.toMap(Pair::getFirst, Pair::getSecond));
    }

    public RecordSet from(String name) {
        RecordSet set = repo.get(name);
        if (set == null) {
            throw new NoSuchElementException(String.format("Cannot found collection%s", name));
        }
        return set;
    }


    public RecRepository select(String expr) {
        return new RecRepository(Selector.of(expr).findAll(this));
    }

    public RecRepository get(String expr) {
        return select(expr);
    }

    public RecordSet get(int index) {
        return collections.get(index);
    }


    @Override
    public Iterator<RecordSet> iterator() {
        return collections.iterator();
    }
}
