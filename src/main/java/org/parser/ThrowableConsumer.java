package org.parser;

@FunctionalInterface
public interface ThrowableConsumer<V, Ex extends Exception> {
    void accept(V v) throws Ex;
}
