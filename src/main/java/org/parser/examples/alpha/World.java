package org.parser.examples.alpha;

import org.apache.commons.lang3.StringUtils;
import org.parser.tree.AST;

import java.util.*;
import java.util.function.BiFunction;

/**
 * The world-class emulates a process: It consists of a memory, registers, stack and a program.
 */
public class World {
    private Map<Register, Value> registers;
    private Map<Address, Value> memory;
    private Stack<Value> stack;
    private Stack<Value> returnStack;
    private final Program program;
    private int pc;

    public World(Program program) {
        this.memory = new HashMap<>();
        this.registers = new HashMap<>();
        this.stack = new Stack<>();
        this.returnStack = new Stack<>();
        this.program = program;
        this.pc = 0;
    }

    /**
     * Use this just for equality checks (the pc is set to -1).
     * @param registers registers
     * @param memory memory
     * @param stack stack
     */
    public World(Map<Register, Value> registers, Map<Address, Value> memory, Stack<Value> stack) throws AlphaError {
        this.registers = registers;
        this.memory = memory;
        this.stack = stack;
        this.returnStack = new Stack<>();
        this.program = new Program();
        this.pc = -1;
    }

    /**
     *
     * @param label Label
     * @return Returns the line number of the given label.
     * @throws AlphaError Throws NullOccurredException if the given label does not exist.
     */
    public int getLineOfLabel(String label) throws AlphaError {
        Integer lineNum = program.getLineOfLabel(label);
        if (lineNum == null) throw new AlphaError.NullOccurredException(label);
        return lineNum;
    }

    /**
     * Clears the world: Meaning everything is set back.
     */
    public void clear() {
        memory = new HashMap<>();
        registers = new HashMap<>();
        stack = new Stack<>();
        returnStack = new Stack<>();
        program.clear();
        pc = 0;
    }

    public String getCurrentLine() {
        return program.getLine(pc);
    }

    public int getPc() {
        return pc;
    }

    /**
     * Pushes the given value onto the stack.
     * @param value Value
     */
    public void load(Value value) {
        push(value);
    }

    /**
     * Pushes the content of the given address. (Default is 0)
     * @param address Address
     */
    public void load(Address address) {
        if (!memory.containsKey(address)) memory.put(address, new Value(0));
        push(memory.get(address));
    }

    /**
     * Pushes the content of the given register. (Default is 0)
     * @param register Register
     */
    public void load(Register register) {
        if (!registers.containsKey(register)) registers.put(register, new Value(0));
        push(registers.get(register));
    }

    /**
     * Stores the given value in the given address.
     * @param address Address
     * @param value Value
     */
    public void store(Address address, Value value) {
        memory.put(address, value);
    }

    /**
     * Stores the given value in the given register.
     * @param register Register
     * @param value Value
     */
    public void store(Register register, Value value) {
        registers.put(register, value);
    }

    /**
     * Pushes the value onto the stack.
     * @param value Value
     */
    public void push(Value value) {
        stack.push(value);
    }

    /**
     * Pops from the stack.
     * @return Returns the top of the stack.
     * @throws AlphaError EmptyStackException is thrown when the stack is empty.
     */
    public Value pop() throws AlphaError {
        if (stack.isEmpty()) throw new AlphaError.EmptyStackException(pc);
        return stack.pop();
    }

    /**
     * Pushes a value onto the return address stack.
     * @param value Value to push
     */
    public void pushReturn(Value value) {
        returnStack.push(value);
    }

    /**
     * Pops from the return address stack.
     * @return Returns the top of the return address stack.
     * @throws AlphaError EmptyStackException is thrown when the stack is empty.
     */
    public Value popReturn() throws AlphaError {
        if (returnStack.isEmpty()) throw new AlphaError.EmptyStackException(pc);
        return returnStack.pop();
    }

    /**
     * Performs a stack operation.
     * Meaning: If the stack is as following x <- y then Operation(y, x) is performed.
     * @param operation Operation to perform (with op_right = top)
     * @throws AlphaError If the stack does not contain at least two elements then an AlphaError is thrown.
     */
    public void stackOp(BiFunction<Value, Value, Value> operation) throws AlphaError {
        Value fst = pop();
        Value scd = pop();
        push(operation.apply(scd, fst));
    }

    /**
     * Performs the action: If the top of the stack is not 0 then the given AST is evaluated. (the top is popped)
     * @param to_do AST to evaluate
     * @throws AlphaError If the stack is empty or the evaluation fails then an AlphaError is thrown.
     */
    public void ifNeq0eval(AST<Type> to_do) throws AlphaError {
        Value top = pop();
        if (top.value() != 0) {
            evalAST(to_do);
        }
    }

    /**
     * Sets the program counter (pc) to the value at the top of the stack.
     * @throws AlphaError If the stack is empty then an AlphaError is thrown.
     */
    public void goto_() throws AlphaError {
        Value top = pop();
        pc = top.value();
    }

    public void gotoLastLine() {
        pc = program.size() - 1;
    }

    /**
     * Evaluates the given AST by calling its evaluation function.
     * @param ast AST
     * @throws AlphaError If the evaluation fails then an AlphaError is thrown.
     */
    public void evalAST(AST<Type> ast) throws AlphaError {
        ast.getType().eval(ast, this);
    }

    /**
     * Executes the program.
     * @throws AlphaError at evaluation failure.
     */
    public void executeProgram() throws AlphaError {
        while (pcInBounds()) {
            executeNextLine();
        }
    }

    /**
     * Executes the line the pc points to (and increments the pc).
     * @throws AlphaError at evaluation failure.
     */
    public void executeNextLine() throws AlphaError {
        if (!pcInBounds()) return;
        AST<Type> line = program.getParsedLine(pc);
        pc++;
        evalAST(line);
    }

    /**
     * Adds a new line to the program.
     * @param line Line
     * @throws AlphaError at parsing failure.
     */
    public void addLine(String line) throws AlphaError {
        program.addLine(line);
    }

    /**
     *
     * @return Returns whether the pc points to an existing line.
     */
    public boolean pcInBounds() {
        return pc >= 0 && pc < program.size();
    }

    /**
     * Unites two worlds by putting all information from the given world in this world (the information from this world
     * may be overridden).
     * @param other Another world
     */
    public void unite(World other) {
        registers.putAll(other.registers);
        memory.putAll(other.memory);
        for (Value elem : other.stack) {
            stack.push(elem);
        }
    }

    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (this == obj) return true;

        if (obj instanceof World other) {
            return Objects.equals(registers, other.registers) && Objects.equals(memory, other.memory)
                    && Objects.equals(stack, other.stack) && Objects.equals(returnStack, other.returnStack);
        }
        return false;
    }

    /**
     *
     * @return Returns all memory of this world as a string.
     */
    public String toString() {
        return StringifyWorld.stringify(this);
    }


    /**
     * Creates a string table out of this world.
     */
    private static class StringifyWorld {
        private static final int colWidth = 24;
        private static final int writableColWidth = 22;
        private static final String v_sep = "|"; // vertical separator
        private static final String h_sep = "-"; // horizontal separator


        public static String stringify(World world) {
            StringBuilder tableBuilder = new StringBuilder();

            createTableHeader(tableBuilder);
            fillTable(world, tableBuilder);

            return tableBuilder.toString();
        }

        private static void createTableHeader(StringBuilder tableBuilder) {
            // column names
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
                fillOneRow(regIt, memIt, stackIt, tableBuilder);
            }
        }

        private static void fillOneRow(Iterator<Map.Entry<Register, Value>> regIt, Iterator<Map.Entry<Address, Value>> memIt,
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
