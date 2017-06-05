package net.kimleo.rec.v2.stream;

import net.kimleo.rec.v2.utils.Streams;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class MergeIteratorTest {

    private final List<Integer> first = Arrays.asList(1, 2, 3, 4, 5);
    private final List<Integer> second = Arrays.asList(2, 4, 6, 8, 10);

    @Test
    public void shouldMerge() throws Exception {

        MergeIterator<Integer> merged = new MergeIterator<>(
                first.iterator(), second.iterator(), Integer::compareTo);

        ArrayList<Integer> ints = new ArrayList<>();

        while (merged.hasNext()) {
            ints.add(merged.next());
        }

        assertThat(ints.get(0), is(1));
        assertThat(ints.get(9), is(10));
        assertThat(ints.get(5), is(4));
    }

    @Test
    public void shouldMergeStream() throws Exception {
        Stream<Integer> merged = Streams.merge(first.stream(), second.stream());

        ArrayList<Integer> ints = new ArrayList<>();

        merged.forEach(ints::add);

        assertThat(ints.get(0), is(1));
        assertThat(ints.get(9), is(10));
        assertThat(ints.get(5), is(4));
    }

    @Test
    public void shouldMergeStreamWithSelfDefinedComparator() throws Exception {

        Comparator<Integer> reversed = (o1, o2) -> o2 - o1;

        Stream<Integer> merged = Streams.merge(
                first.stream().sorted(reversed),
                second.stream().sorted(reversed),
                reversed);

        ArrayList<Integer> ints = new ArrayList<>();

        merged.forEach(ints::add);

        assertThat(ints.get(0), is(10));
        assertThat(ints.get(9), is(1));
        assertThat(ints.get(5), is(4));
    }


}