package net.kimleo.rec.util;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Function;
import java.util.function.Predicate;

public class CollectUtils {
    public static <T> Iterable<T> reversed(List<T> list) {


        return () -> {
            ListIterator<T> li = list.listIterator(list.size());

            return new Iterator<T>() {
                @Override
                public boolean hasNext() {
                    return li.hasPrevious();
                }

                @Override
                public T next() {
                    return li.previous();
                }
            };
        };
    }

    public static <T, U> Iterable<U> mapped(Iterable<T> it, Function<T, U> mapper) {
        return () -> {
            Iterator<T> iterator = it.iterator();

            return new Iterator<U>() {
                @Override
                public boolean hasNext() {
                    return iterator.hasNext();
                }

                @Override
                public U next() {
                    return mapper.apply(iterator.next());
                }
            };
        };
    }

    public static <E extends Enum<E>> Predicate<E> between(E left, E right) {
        return (e) -> e.compareTo(left) > 0 && e.compareTo(right) < 0;
    }
}
