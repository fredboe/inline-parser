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
        world.evalAST(ast.getChild(0)); // evaluate assignable
        world.evalAST(ast.getChild(1)); // evaluate expr
        var to_store = world.pop();
        var where_to_store = world.pop();
        where_to_store.setValue(to_store.value());
    }),
    EXPR((ast, world) -> {
        world.evalAST(ast.getChild(0)); // evaluate op_left
        world.evalAST(ast.getChild(2)); // evaluate op_right
        world.evalAST(ast.getChild(1)); // stack op
    }),
    CONDITION((ast, world) -> {
        world.evalAST(ast.getChild(0)); // evaluate op_left
        world.evalAST(ast.getChild(2)); // evaluate op_right
        world.evalAST(ast.getChild(1)); // stack op (comp)
    }),
    CALL((ast, world) -> {
        world.push(new Value(world.getPc()));
        world.evalAST(ast.getChild(0)); // push line_to_go
        world.goto_(); // goto top of stack
    }),
    RETURN((ast, world) -> world.goto_()),
    ACCUMULATOR((ast, world) -> world.load(new Register(Integer.parseInt(matched(ast))))),
    ADDRESS((ast, world) -> {
        world.evalAST(ast.getChild(0));
        var address = new Address(world.pop());
        world.load(address);
    }),
    NUMBER((ast, world) -> world.load(new Value(Integer.parseInt(matched(ast))))),
    LABEL((ast, world) -> world.load(new Value(world.getLineOfLabel(matched(ast))))),
    PUSH((ast, world) -> world.evalAST(ast.getChild(0))),
    POP((ast, world) -> {
        world.evalAST(ast.getChild(0));
        var where_to_store = world.pop();
        var top_value = world.pop();
        where_to_store.setValue(top_value);
    }),
    STACK_OP((ast, world) -> world.evalAST(ast.getChild(0))),
    ADD((ast, world) -> world.stackOp(Value::add)),
    SUB((ast, world) -> world.stackOp(Value::sub)),
    MUL((ast, world) -> world.stackOp(Value::mul)),
    DIV((ast, world) -> world.stackOp(Value::div)),
    MOD((ast, world) -> world.stackOp(Value::mod)),
    LEQ((ast, world) -> world.stackOp(Value::leq)),
    GEQ((ast, world) -> world.stackOp(Value::geq)),
    LE((ast, world) -> world.stackOp(Value::le)),
    GE((ast, world) -> world.stackOp(Value::ge)),
    EQ((ast, world) -> world.stackOp(Value::eq)),
    END((ast, world) -> world.load(new Value(-1))); // push -1 onto the stack

    private final ThrowableBiConsumer<AST<Type>, World, AlphaError> transformer;

    Type(ThrowableBiConsumer<AST<Type>, World, AlphaError> transformer) {
        this.transformer = transformer;
    }

    public void eval(AST<Type> ast, World world) throws AlphaError {
        transformer.accept(ast, world);
    }

    private static String matched(AST<Type> ast) throws AlphaError {
        var match = ast.getMatch();
        if (match == null) throw new AlphaError.NullOccurredException(ast);
        return match.matched();
    }
}
