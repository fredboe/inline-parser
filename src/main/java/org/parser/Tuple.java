package org.parser;

public record Tuple<U, V> (U u, V v) {
    public static <U, V> Tuple<U, V> of(U u, V v) {
        return new Tuple<>(u, v);
    }
}
