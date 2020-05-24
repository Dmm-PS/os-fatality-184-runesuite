package io.ruin.api.utils;

public class ArrayUtils {

    @SafeVarargs
    public static <T> T[] of(T... values) {
        return values;
    }

}
