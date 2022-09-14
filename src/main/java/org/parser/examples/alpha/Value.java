package org.parser.examples.alpha;

/**
 * Value class contains an int as value (this may be changed in the future).
 */
public class Value {
    private int value;

    public Value(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public void setValue(Value value) {
        this.value = value.value();
    }

    public String toString() {
        return Integer.toString(value);
    }

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

    public static Value leq(Value opl, Value opr) {
        return new Value(boolToInt(opl.value() <= opr.value()));
    }

    public static Value geq(Value opl, Value opr) {
        return new Value(boolToInt(opl.value() >= opr.value()));
    }

    public static Value le(Value opl, Value opr) {
        return new Value(boolToInt(opl.value() < opr.value()));
    }

    public static Value ge(Value opl, Value opr) {
        return new Value(boolToInt(opl.value() > opr.value()));
    }

    public static Value eq(Value opl, Value opr) {
        return new Value(boolToInt(opl.value() == opr.value()));
    }


    private static int boolToInt(boolean bool) {
        return bool ? 1 : 0;
    }
}
