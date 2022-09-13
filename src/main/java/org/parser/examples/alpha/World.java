package org.parser.examples.alpha;

import org.apache.commons.lang3.StringUtils;
import org.parser.tree.AST;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;
import java.util.function.BiFunction;

public class World {
    private Map<Address, Value> memory;
    private Map<Register, Value> registers;
    private Stack<Value> stack;
    private final AlphaProgram program;
    private int pc;

    public World(AlphaProgram program) {
        this.memory = new HashMap<>();
        this.registers = new HashMap<>();
        this.stack = new Stack<>();
        this.program = program;
        this.pc = 0;
    }

    public int getLineOfLabel(String label) throws AlphaError {
        Integer lineNum = program.getLineOfLabel(label);
        if (lineNum == null) throw new AlphaError.NullOccurredException(label);
        return lineNum;
    }

    public void clear() {
        memory = new HashMap<>();
        registers = new HashMap<>();
        stack = new Stack<>();
        program.clear();
        pc = 0;
    }

    public String getCurrentLine() {
        return program.getLine(pc);
    }

    public int getPc() {
        return pc;
    }

    public void load(Value value) {
        push(value);
    }

    public void load(Address address) {
        if (!memory.containsKey(address)) memory.put(address, new Value(0));
        push(memory.get(address));
    }

    public void load(Register register) {
        if (!registers.containsKey(register)) registers.put(register, new Value(0));
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

    public Value pop() throws AlphaError {
        if (stack.isEmpty()) throw new AlphaError.EmptyStackException(pc);
        return stack.pop();
    }

    public void stackOp(BiFunction<Value, Value, Value> operation) throws AlphaError {
        Value top = pop();
        Value bot = pop();
        push(operation.apply(bot, top));
    }

    public void ifNeq0eval(AST<Type> todo) throws AlphaError {
        Value top = pop();
        if (top.value() != 0) {
            evalAST(todo);
        }
    }

    public void goto_() throws AlphaError {
        Value top = pop();
        pc = top.value();
    }

    public void evalAST(AST<Type> ast) throws AlphaError {
        ast.getType().eval(ast, this);
    }

    public void evalProgram() throws AlphaError {
        while (executeNextLine()) {}
    }

    public boolean executeNextLine() throws AlphaError {
        if (!pcInBounds()) return false;

        AST<Type> line = program.getParsedLine(pc);
        pc++;
        evalAST(line);
        return true;
    }

    public boolean pcInBounds() {
        return pc >= 0 && pc < program.size();
    }

    public String toString() {
        return StringifyWorld.stringify(this);
    }

    private static class StringifyWorld {
        private static final int colWidth = 30;
        private static final int writableColWidth = 28;
        private static final String v_sep = "│"; // vertical separator
        private static final String h_sep = "┄"; // horizontal separator

        public static String stringify(World world) {
            StringBuilder tableBuilder = new StringBuilder();

            createTableHeader(tableBuilder);
            fillTable(world, tableBuilder);

            return tableBuilder.toString();
        }

        private static void createTableHeader(StringBuilder tableBuilder) {
            tableBuilder.append(v_sep).append(center("Register")).append(v_sep).append(center("Memory")).append(v_sep)
                    .append(center("Stack")).append(v_sep).append('\n');

            // horizontal line
            tableBuilder.append(v_sep).append(repeat(h_sep)).append(v_sep).append(repeat(h_sep)).append(v_sep)
                    .append(repeat(h_sep)).append(v_sep).append('\n');
        }

        private static void fillTable(World world, StringBuilder tableBuilder) {
            var regIt = world.registers.entrySet().iterator();
            var memIt = world.memory.entrySet().iterator();
            var stackIt = world.stack.iterator();

            while (regIt.hasNext() || memIt.hasNext() || stackIt.hasNext()) {
                filleOneRow(regIt, memIt, stackIt, tableBuilder);
            }
        }

        private static void filleOneRow(Iterator<Map.Entry<Register, Value>> regIt, Iterator<Map.Entry<Address, Value>> memIt,
                                        Iterator<Value> staIt, StringBuilder tableBuilder) {
            tableBuilder.append(v_sep);
            tableBuilder.append(nextReg(regIt));
            tableBuilder.append(v_sep);
            tableBuilder.append(nextMem(memIt));
            tableBuilder.append(v_sep);
            tableBuilder.append(nextStack(staIt));
            tableBuilder.append(v_sep);
            tableBuilder.append('\n');
        }

        private static String nextReg(Iterator<Map.Entry<Register, Value>> regIt) {
            String toExpand = regIt.hasNext() ? toString(regIt.next()) : "";
            return rightPad(toExpand);
        }


        private static String nextMem(Iterator<Map.Entry<Address, Value>> memIt) {
            String toExpand = memIt.hasNext() ? toString(memIt.next()) : "";
            return rightPad(toExpand);
        }

        private static String nextStack(Iterator<Value> staIt) {
            String toExpand = staIt.hasNext() ? staIt.next().toString() : "";
            return center(toExpand);
        }

        public static String toString(Map.Entry<?, ?> entry) {
            return entry.getValue() + " in " + entry.getKey();
        }

        private static String center(String s) {
            return StringUtils.center(StringUtils.abbreviate(s, writableColWidth), colWidth);
        }

        private static String rightPad(String s) {
            return StringUtils.rightPad(" " + s, colWidth);
        }

        public static String repeat(String s) {
            return StringUtils.repeat(s, colWidth);
        }
    }
}
