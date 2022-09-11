package org.parser.examples.alpha;

public record Address(int address) {
    public Address(Value value) {
        this(value.value());
    }
}
