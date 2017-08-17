package net.kimleo.rec.v2.utils;

import net.kimleo.rec.v2.stream.MergeIterator;
import net.kimleo.rec.v2.stream.adapter.IteratingSpliteratorAdapter;

import java.util.Comparator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Streams {
    public static <T extends Comparable<T>> Stream<T> merge(Stream<T> first,
                                                            Stream<T> second) {
        MergeIterator<T> iterator = new MergeIterator<>(
                first.iterator(), second.iterator(), Comparable::compareTo);
        IteratingSpliteratorAdapter<T> adapter = new IteratingSpliteratorAdapter<>(iterator);
        return StreamSupport.stream(adapter, false);
    }

    public static <T extends Comparable<T>> Stream<T> merge(Stream<T> first,
                                                            Stream<T> second,
                                                            Comparator<T> comparator) {
        MergeIterator<T> iterator = new MergeIterator<>(
                first.iterator(), second.iterator(), comparator);
        IteratingSpliteratorAdapter<T> adapter = new IteratingSpliteratorAdapter<>(iterator);
        return StreamSupport.stream(adapter, false);
    }

}
