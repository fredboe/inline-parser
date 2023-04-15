package org.parser.base;

import org.parser.Consumable;
import org.parser.tree.AST;

import java.util.Optional;
import java.util.Stack;
import java.util.function.Consumer;

public class Environment<TYPE> {
    private final Stack<Optional<AST<TYPE>>> resultStack;
    private final Stack<Consumer<Consumable>> callbackStack;
    private final Stack<Parser<TYPE>> parserStack;

    private final Consumable consumable;

    public Environment(Consumable consumable) {
        this.resultStack = new Stack<>();
        this.callbackStack = new Stack<>();
        this.parserStack = new Stack<>();
        this.consumable = consumable;
    }

    Stack<Optional<AST<TYPE>>> resultStack() {
        return resultStack;
    }

    void executeAndThenCall(Parser<TYPE> parser, Consumer<Consumable> callback) {
        callbackStack.push(callback);
        parserStack.push(parser);
    }

    Consumable.Mark createConsumableMark() {
        return consumable.mark();
    }

    private void run() {
        while (!parserStack.isEmpty() || !callbackStack.isEmpty()) {
            while (!parserStack.isEmpty()) {
                var parser = parserStack.pop();
                if (parser != null) parser.processWith(this);
            }
            if (!callbackStack.isEmpty()) {
                var callback = callbackStack.pop();
                callback.accept(consumable);
            }
        }
    }

    public Optional<AST<TYPE>> startWith(Parser<TYPE> parser) {
        executeAndThenCall(parser, (consumable) -> {});
        run();
        return resultStack.pop();
    }
}
