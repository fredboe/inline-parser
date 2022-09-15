package org.parser.examples.alpha;

import org.parser.ThrowableBiConsumer;
import org.parser.tree.AST;


public enum Type {
    PROGRAM((ast, world) -> {}),
    LABELED((ast, world) -> {}),
    BRANCH((ast, world) -> {
        world.evalAST(ast.getChild(0)); // evaluate condition
        world.ifNeq0eval(ast.getChild(1)); // if condition is not 0 evaluate goto
    }),
    GOTO((ast, world) -> {
        world.evalAST(ast.getChild(0)); // push line_to_go
        world.goto_(); // goto top of stack
    }),
    ASSIGN((ast, world) -> {
        world.evalAST(ast.getChild(0)); // push assignable
        world.evalAST(ast.getChild(1)); // push expr
        var exprValue = world.pop();
        var assignableValue = world.pop();
        store(assignableValue, exprValue); // stores a copy of exprValue in assignableValue
    }),
    EXPR((ast, world) -> {
        world.evalAST(ast.getChild(0)); // push op_left
        world.evalAST(ast.getChild(2)); // push op_right
        world.evalAST(ast.getChild(1)); // stack op
    }),
    CONDITION((ast, world) -> {
        world.evalAST(ast.getChild(0)); // push op_left
        world.evalAST(ast.getChild(2)); // push op_right
        world.evalAST(ast.getChild(1)); // stack op (comparison)
    }),
    CALL((ast, world) -> {
        world.push(new Value(world.getPc())); // push return address (pc has already been incremented)
        world.evalAST(ast.getChild(0)); // push first line of subroutine
        world.goto_(); // goto top of stack
    }),
    RETURN((ast, world) -> world.goto_()), // goto top of stack (return address)
    ACCUMULATOR((ast, world) -> world.load(new Register(Integer.parseInt(matchOf(ast))))), // push value of accumulator onto the stack
    ADDRESS((ast, world) -> {
        world.evalAST(ast.getChild(0)); // push address
        var address = new Address(world.pop());
        world.load(address); // push value of address
    }),
    NUMBER((ast, world) -> world.load(new Value(Integer.parseInt(matchOf(ast))))), // push number
    LABEL((ast, world) -> world.load(new Value(world.getLineOfLabel(matchOf(ast))))), // push line number of label
    PUSH((ast, world) -> world.evalAST(ast.getChild(0))),
    POP((ast, world) -> {
        world.evalAST(ast.getChild(0)); // push where_to_store
        var where_to_store = world.pop();
        var top_value = world.pop();
        store(where_to_store, top_value); // stores a copy of the top stack element in where_to_store
    }),
    STACK_OP((ast, world) -> world.evalAST(ast.getChild(0))), // evaluate given operator
    ADD((ast, world) -> world.stackOp(Value::add)), // op_left and op_right must have been pushed before
    SUB((ast, world) -> world.stackOp(Value::sub)),
    MUL((ast, world) -> world.stackOp(Value::mul)),
    DIV((ast, world) -> world.stackOp(Value::div)),
    MOD((ast, world) -> world.stackOp(Value::mod)),
    LEQ((ast, world) -> world.stackOp(Value::leq)),
    GEQ((ast, world) -> world.stackOp(Value::geq)),
    LE((ast, world) -> world.stackOp(Value::le)),
    GE((ast, world) -> world.stackOp(Value::ge)),
    EQ((ast, world) -> world.stackOp(Value::eq)),
    END((ast, world) -> {
        world.push(new Value(-1));
        world.goto_(); // goto -1
    }),
    MEM((ast, world) -> IO.info(world.toString())),
    CLEAR((ast, world) -> {
        world.clear();
        IO.info("Memory has been cleared!");
    }),
    PRINT((ast, world) -> {
        world.evalAST(ast.getChild(0)); // push what_to_print
        IO.info(world.pop());
    }),
    EXE((ast, world) -> {
        World programWorld = IO.loadProgram(matchOf(ast));
        programWorld.executeProgram();
        world.unite(programWorld); // store the memory of the executed program in the current world
    }),
    LOAD((ast, world) -> {});

    private final ThrowableBiConsumer<AST<Type>, World, AlphaError> transformer;

    Type(ThrowableBiConsumer<AST<Type>, World, AlphaError> transformer) {
        this.transformer = transformer;
    }

    /**
     * Evaluates the given AST (transforms the world).
     * @param ast AST
     * @param world World to transform
     */
    public void eval(AST<Type> ast, World world) throws AlphaError {
        transformer.accept(ast, world);
    }

    /**
     *
     * @param ast AST
     * @return Returns the match-string of the AST.
     * @throws AlphaError Throws NullOccurredException if the match-string is null.
     */
    private static String matchOf(AST<Type> ast) throws AlphaError {
        var match = ast.getMatch();
        if (match == null) throw new AlphaError.NullOccurredException(ast);
        return match.matched();
    }

    /**
     * Stores a copy of to_store in to_assign.
     * @param to_assign Updated this value
     * @param to_store Value to store in to_assign
     */
    private static void store(Value to_assign, Value to_store) {
        to_assign.setValue(new Value(to_store));
    }
}
