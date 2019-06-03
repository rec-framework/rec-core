package net.kimleo.rec.common.collection;

import java.util.Iterator;
import java.util.function.Function;

public class MappedIterator<T, U> implements Iterator<U> {

    private final Iterator<T> iterator;
    private final Function<T, U> mapper;

    MappedIterator(Iterable<T> iterable, Function<T, U> mapper) {
        this.iterator = iterable.iterator();
        this.mapper = mapper;
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public U next() {
        return mapper.apply(iterator.next());
    }
}
