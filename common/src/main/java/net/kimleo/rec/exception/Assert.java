package net.kimleo.rec.exception;

import static java.lang.String.format;

public class Assert {
    public static void assertTrue(boolean test) {
        if (!test) {
            throw new AssertionError("Expected to be true, but got false.");
        }
    }

    public static void assertEquals(Object left, Object right) {
        if (!left.equals(right)) {
            throw new AssertionError(format("Expect %s equals %s failed.", left, right));
        }
    }

    public static void assertNotNull(Object obj) {
        if (obj == null) {
            throw new AssertionError("Expect not null failed.");
        }
    }

    public static void fail() {
        throw new AssertionError("Unexpected call to fail.");
    }
}
