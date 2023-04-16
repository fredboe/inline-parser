package org.parser.base;

import org.parser.Consumable;
import org.parser.Pair;
import org.parser.tree.AST;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Stack;
import java.util.function.Consumer;

public class Environment<TYPE> {
    private final Stack<Optional<AST<TYPE>>> resultStack;
    private final Stack<Consumer<Consumable>> callbackStack;
    private final Stack<Parser<TYPE>> parserStack;
    private final Consumable consumable;
    private final Map<Pair<Consumable.Mark, String>, Pair<Consumable.Mark, Optional<AST<TYPE>>>> cache;

    public Environment(Consumable consumable) {
        this.resultStack = new Stack<>();
        this.callbackStack = new Stack<>();
        this.parserStack = new Stack<>();
        this.consumable = consumable;
        this.cache = new HashMap<>();
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

    void putToCache(Consumable.Mark startMark, String name, Optional<AST<TYPE>> optionalAST) {
        Consumable.Mark endMark = consumable.mark();
        cache.put(new Pair<>(startMark, name), new Pair<>(endMark, optionalAST));
    }

    boolean hasInCache(Consumable.Mark startMark, String name) {
        return cache.containsKey(new Pair<>(startMark, name));
    }

    Optional<AST<TYPE>> getFromCache(Consumable.Mark startMark, String name) {
        Pair<Consumable.Mark, String> key = new Pair<>(startMark, name);
        Pair<Consumable.Mark, Optional<AST<TYPE>>> cachedValue = cache.get(key);

        if (cachedValue != null) {
            var endMark = cachedValue.x();
            var optionalAST = cachedValue.y();
            consumable.gotoMark(endMark);
            return optionalAST;
        }

        return Optional.empty();
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
