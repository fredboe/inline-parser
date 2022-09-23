package org.parser.base.build;

import org.parser.Tuple;
import org.parser.tree.AST;

import java.util.HashMap;
import java.util.Map;

public class Memoization<TYPE> {
    private Map<Tuple<Integer, TYPE>, Tuple<AST<TYPE>, Integer>> memoWithType;
    private Map<Tuple<Integer, String>, Tuple<AST<TYPE>, Integer>> memoWithName;

    public Memoization() {
        memoWithType = new HashMap<>();
        memoWithName = new HashMap<>();
    }

    public void clear() {
        memoWithType = new HashMap<>();
        memoWithName = new HashMap<>();
    }

    public boolean containsKey(int startIndex, TYPE type) {
        return memoWithType.containsKey(Tuple.of(startIndex, type));
    }

    public boolean containsKey(int startIndex, String name) {
        return memoWithName.containsKey(Tuple.of(startIndex, name));
    }

    public Tuple<AST<TYPE>, Integer> get(int startIndex, TYPE type) {
        return memoWithType.get(Tuple.of(startIndex, type));
    }

    public Tuple<AST<TYPE>, Integer> get(int startIndex, String name) {
        return memoWithName.get(Tuple.of(startIndex, name));
    }

    public void put(int startIndex, TYPE type, AST<TYPE> to_memo, int length) {
        memoWithType.put(Tuple.of(startIndex, type), Tuple.of(to_memo, length));
    }

    public void put(int startIndex, String name, AST<TYPE> to_memo, int length) {
        memoWithName.put(Tuple.of(startIndex, name), Tuple.of(to_memo, length));
    }
}
