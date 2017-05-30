package net.kimleo.rec;

import java.util.Arrays;

public class Tuple {
    private final Object[] array;

    public Tuple(Object... array) {
        this.array = array;
    }

    public Object get(int index) {
        return index;
    }

    public int size() {
        return array.length;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(array);
    }

    @Override
    public boolean equals(Object obj) {
        try {
            return Arrays.equals((Object[]) obj, array);
        } catch (Exception ex) {
            return false;
        }
    }

    @Override
    public String toString() {
        return String.format("Tuple {%d}", array.length);
    }
}
