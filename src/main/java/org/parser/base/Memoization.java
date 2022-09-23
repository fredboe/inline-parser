package org.parser.base;

import org.parser.Tuple;
import org.parser.tree.AST;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Memoization<TYPE> {
    private final Map<Tuple<String, Integer>, Tuple<Optional<AST<TYPE>>, Integer>> memo;

    public Memoization() {
        memo = new HashMap<>();
    }

    public boolean isMemoized(String name, int where) {
        return memo.containsKey(Tuple.of(name, where));
    }

    public Tuple<Optional<AST<TYPE>>, Integer> get(String name, int where) {
        return memo.get(Tuple.of(name, where));
    }

    public void memoize(String name, int where, AST<TYPE> ast, int length) {
        memo.put(Tuple.of(name, where), Tuple.of(Optional.ofNullable(ast), length));
    }
}
