package org.parser.examples.alpha;

public record Value(int value) {
    public static Value add(Value opl, Value opr) {
        return new Value(opl.value() + opr.value());
    }

    public static Value sub(Value opl, Value opr) {
        return new Value(opl.value() - opr.value());
    }

    public static Value mul(Value opl, Value opr) {
        return new Value(opl.value() * opr.value());
    }

    public static Value div(Value opl, Value opr) {
        return new Value(opl.value() / opr.value());
    }

    public static Value mod(Value opl, Value opr) {
        return new Value(opl.value() % opr.value());
    }
}
