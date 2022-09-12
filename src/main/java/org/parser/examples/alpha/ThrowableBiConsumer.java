package org.parser.examples.alpha;

@FunctionalInterface
public interface ThrowableBiConsumer<T, U, Ex extends Exception> {
    void accept(T t, U u) throws Ex;
}
