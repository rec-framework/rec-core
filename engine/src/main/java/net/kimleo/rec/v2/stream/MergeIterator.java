package net.kimleo.rec.v2.stream;

import java.util.Comparator;
import java.util.Iterator;

public class MergeIterator<T> implements Iterator<T> {

    private final Iterator<T> first;
    private final Iterator<T> second;
    private T next1;
    private T next2;

    private final Comparator<T> comparator;

    public MergeIterator(Iterator<T> first, Iterator<T> second, Comparator<T> comparator) {
        this.first = first;
        this.second = second;
        next1 = first.hasNext() ? first.next() : null;
        next2 = second.hasNext() ? second.next() : null;
        this.comparator = comparator;
    }

    @Override
    public T next() {

        if(!hasNext()) {
            return null;
        }

        boolean useStream1 = (next1 != null && next2 == null)  ||
                (next1 != null && comparator.compare(next1, next2) <= 0);

        if(useStream1) {
            T returnObject = next1;
            next1 = first.hasNext() ? first.next() : null;
            return returnObject;
        }
        else {
            T returnObject = next2;
            next2 = second.hasNext() ? second.next() : null;
            return returnObject;
        }
   }

    @Override
    public boolean hasNext() {
        return next1 != null || next2 != null;
    }
}
