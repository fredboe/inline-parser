package org.parser.examples.alpha;

public record Register(int register) {
    public Register(Value value) {
        this(value.value());
    }
}