package org.parser.alpha;

/**
 * Address class contains just the address (not the content).
 * @param address Address
 */
public record Address(int address) {
    public Address(Value value) {
        this(value.value());
    }

    public String toString() {
        return "adr=" + address;
    }
}
