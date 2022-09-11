package org.parser.examples.alpha;

import org.parser.tree.AST;

import java.util.function.BiConsumer;

public enum Type {
    PROGRAM((ast, world) -> {}),
    LABELED((ast, world) -> {}),
    BRANCH((ast, world) -> {
        world.eval(ast.getChild(0)); // evaluate condition
        world.ifNeq0eval(ast.getChild(1)); // if condition is not 0 evaluate goto
    }),
    GOTO((ast, world) -> {
        world.eval(ast.getChild(0)); // push line_to_go
        world.goto_(); // goto top of stack
    }),
    ASSIGN((ast, world) -> {
        world.eval(ast.getChild(0)); // evaluate assignable
        world.eval(ast.getChild(1)); // evaluate expr
        var to_store = world.pop();
        var where_to_store = world.pop();
        where_to_store.setValue(to_store.value());
    }),
    EXPR((ast, world) -> {
        world.eval(ast.getChild(0)); // evaluate op_left
        world.eval(ast.getChild(2)); // evaluate op_right
        world.eval(ast.getChild(1)); // stack op
    }),
    CONDITION((ast, world) -> {
        world.eval(ast.getChild(0)); // evaluate op_left
        world.eval(ast.getChild(2)); // evaluate op_right
        world.eval(ast.getChild(1)); // stack op (comp)
    }),
    CALL((ast, world) -> {
        world.eval(ast.getChild(0)); // push line_to_go
        world.goto_(); // goto top of stack
    }),
    RETURN((ast, world) -> world.goto_()),
    ACCUMULATOR((ast, world) -> {
        var optionalMatch = ast.getMatch();
        optionalMatch.ifPresent(match -> world.load(new Register(Integer.parseInt(match.matched()))));
    }),
    ADDRESS((ast, world) -> {
        world.eval(ast.getChild(0));
        var address = new Address(world.pop());
        world.load(address);
    }),
    NUMBER((ast, world) -> {
        var optionalMatch = ast.getMatch();
        optionalMatch.ifPresent(match -> world.load(new Value(Integer.parseInt(match.matched()))));
    }),
    LABEL((ast, world) -> {
        var optionalMatch = ast.getMatch();
        optionalMatch.ifPresent(label -> world.load(new Value(world.getLineOfLabel(label.matched()))));
    }),
    PUSH((ast, world) -> world.eval(ast.getChild(0))),
    POP((ast, world) -> {
        world.eval(ast.getChild(0));
        var where_to_store = world.pop();
        var top_value = world.pop();
        where_to_store.setValue(top_value);
    }),
    STACK_OP((ast, world) -> world.eval(ast.getChild(0))),
    ADD((ast, world) -> world.stackOp(Value::add)),
    SUB((ast, world) -> world.stackOp(Value::sub)),
    MUL((ast, world) -> world.stackOp(Value::mul)),
    DIV((ast, world) -> world.stackOp(Value::div)),
    MOD((ast, world) -> world.stackOp(Value::mod)),
    LEQ((ast, world) -> world.stackOp(Value::leq)),
    GEQ((ast, world) -> world.stackOp(Value::geq)),
    LE((ast, world) -> world.stackOp(Value::le)),
    GE((ast, world) -> world.stackOp(Value::ge)),
    EQ((ast, world) -> world.stackOp(Value::eq));

    // operations funktionieren mit push und stack_op

    private final BiConsumer<AST<Type>, World> transformer;

    Type(BiConsumer<AST<Type>, World> transformer) {
        this.transformer = transformer;
    }

    public void eval(AST<Type> ast, World world) {
        transformer.accept(ast, world);
    }
}