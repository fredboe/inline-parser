package org.parser;

public record Tuple<X, Y>(X x, Y y) {
    public static <X, Y> Tuple<X, Y> of(X x, Y y) {
        return new Tuple<>(x, y);
    }
}
