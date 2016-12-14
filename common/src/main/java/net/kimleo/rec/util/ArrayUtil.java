package net.kimleo.rec.util;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.stream.Stream;

public class ArrayUtil {
    public static <T> Stream<T> drop(T[] array, long amount) {
        assert amount > 0;

        return Arrays.stream(array).skip(amount);
    }
}
