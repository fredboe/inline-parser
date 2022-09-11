package org.parser.examples.alpha;

import org.parser.tree.AST;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.function.BiFunction;

public class World {
    private final Map<Address, Value> memory;
    private final Map<Register, Value> registers;
    private final Stack<Value> stack;
    private final Program program;
    private int pc;

    private static final Value defaultValue = new Value(0);

    public World(Program program) {
        this.memory = new HashMap<>();
        this.registers = new HashMap<>();
        this.stack = new Stack<>();
        this.program = program;
        this.pc = 0;
    }

    public Program program() {
        return program;
    }

    public int getLineOfLabel(String label) {
        return program().getLineOfLabel(label);
    }

    public void load(Value value) {
        push(value);
    }

    public void load(Address address) {
        if (!memory.containsKey(address)) memory.put(address, defaultValue);
        push(memory.get(address));
    }

    public void load(Register register) {
        if (!registers.containsKey(register)) registers.put(register, defaultValue);
        push(registers.get(register));
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

    public void stackOp(BiFunction<Value, Value, Value> operation) {
        Value top = pop();
        Value bot = pop();
        push(operation.apply(bot, top));
    }

    public void ifNeq0eval(AST<Type> todo) {
        Value top = pop();
        if (top.value() != 0) {
            eval(todo);
        }
    }

    public void goto_() {
        Value top = pop();
        pc = top.value();
    }

    public void eval(AST<Type> ast) {
        ast.getType().eval(ast, this);
    }

    public void eval() {
        while (executeNextLine()) {
            System.out.println("PC:" + pc);
        }
    }

    public boolean executeNextLine() {
        if (pc >= 0 && pc < program.size()) {
            AST<Type> line = program.getLine(pc);
            pc++;
            eval(line);
            return true;
        }
        return false;
    }

    public String toString() {
        return "Stack:\n" + stack + "\nMemory:\n" + memory + "\nRegisters:\n" + registers;
    }
}
