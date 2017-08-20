package net.kimleo.rec.util;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

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
}
