package net.kimleo.rec.v2.stream.adapter;

import java.util.Iterator;
import java.util.Spliterators;
import java.util.function.Consumer;

public class IteratingSpliteratorAdapter<T> extends Spliterators.AbstractSpliterator<T> {

    private final Iterator<T> iterator;

    public IteratingSpliteratorAdapter(Iterator<T> iterator) {
        super(Long.MAX_VALUE, 0);
        this.iterator = iterator;
    }

    @Override
    public boolean tryAdvance(Consumer<? super T> action) {
        if (iterator.hasNext()) {
            action.accept(iterator.next());
            return true;
        }
        return false;
    }
}
