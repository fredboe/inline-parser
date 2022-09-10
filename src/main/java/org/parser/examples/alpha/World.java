package org.parser.examples.alpha;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.function.BiFunction;
import java.util.function.Function;

public class World {
    private final Map<Address, Value> memory;
    private final Map<Register, Value> registers;
    private final Stack<Value> stack;

    public enum Operation {
        ADD((opl, opr) -> Value.add(opl, opr)),
        SUB((opl, opr) -> Value.sub(opl, opr)),
        MUL((opl, opr) -> Value.mul(opl, opr)),
        DIV((opl, opr) -> Value.div(opl, opr)),
        MOD((opl, opr) -> Value.mod(opl, opr));

        private final BiFunction<Value, Value, Value> operation;

        Operation(BiFunction<Value, Value, Value> operation) {
            this.operation = operation;
        }

        public Value apply(Value opl, Value opr) {
            return operation.apply(opl, opr);
        }
    }

    public World() {
        memory = new HashMap<>();
        registers = new HashMap<>();
        stack = new Stack<>();
    }

    public Value load(Address address) {
        return memory.get(address);
    }

    public Value load(Register register) {
        return registers.get(register);
    }

    public void store(Address address, Value value) {
        memory.put(address, value);
    }

    public void store(Register register, Value value) {
        registers.put(register, value);
    }

    public void push(Value value) {
        stack.push(value);
    }

    public Value pop() {
        return !stack.isEmpty() ? stack.pop() : null;
    }

    public void stackOp(Operation operation) {
        Value up = pop();
        Value low = pop();
        push(operation.apply(low, up));
    }
}
