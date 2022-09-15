package org.parser.examples.alpha;

/**
 * Register class contains just the register number (not the content).
 * @param register register number
 */
public record Register(int register) {
    public Register(Value value) {
        this(value.value());
    }

    public String toString() {
        return "reg=" + register;
    }
}
