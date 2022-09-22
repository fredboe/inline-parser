package org.parser.base;

import org.parser.Consumable;
import org.parser.Tuple;
import org.parser.tree.AST;

import java.util.HashMap;
import java.util.Map;

public class Memoization<TYPE> {
    private final Map<Tuple<Integer, String>, Tuple<AST<TYPE>, Integer>> memo;

    public Memoization() {
        memo = new HashMap<>();
    }

    public boolean isMemoized(int startIndex, String name) {
        return memo.containsKey(Tuple.of(startIndex, name));
    }

    public Tuple<AST<TYPE>, Integer> get(int startIndex, String name) {
        return memo.get(Tuple.of(startIndex, name));
    }

    public void memoize(int startIndex, String name, AST<TYPE> ast, int length) {
        memo.put(Tuple.of(startIndex, name), Tuple.of(ast, length));
    }
}
