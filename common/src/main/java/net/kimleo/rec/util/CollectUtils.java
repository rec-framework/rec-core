package net.kimleo.rec.util;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Stream;

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
