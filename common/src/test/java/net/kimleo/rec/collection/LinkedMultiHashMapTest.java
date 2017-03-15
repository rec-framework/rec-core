package net.kimleo.rec.collection;

import org.junit.Test;

import java.util.Arrays;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;

public class LinkedMultiHashMapTest {
    @Test
    public void shouldBuildAMapSuccessfully() throws Exception {
        MultiMap<Integer, Integer> map = LinkedMultiHashMap.from(Stream.of(1, 1, 1, 2, 2, 2, 3, 3, 3), x -> x);

        assertThat(map.size(), is(3));

        assertThat(map.keyCount(), is(3));
        assertThat(map.valueCount(), is(3));

        assertTrue(map.get(1).contains(1));

        map.put(4, Arrays.asList(1, 2, 3));

        assertThat(map.size(), is(6));
    }
}