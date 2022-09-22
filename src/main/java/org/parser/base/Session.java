package org.parser.base;

import org.parser.Tuple;
import org.parser.tree.AST;

import java.util.HashMap;
import java.util.Map;

public class Session<TYPE> {
    private Map<Tuple<Integer, TYPE>, Tuple<AST<TYPE>, Integer>> memoWithType;

    public Session() {
        memoWithType = new HashMap<>();
    }

    public void clear() {
        memoWithType = new HashMap<>();
    }

    public boolean isMemoized(int startIndex, TYPE type) {
        return memoWithType.containsKey(Tuple.of(startIndex, type));
    }

    public Tuple<AST<TYPE>, Integer> get(int startIndex, TYPE type) {
        return memoWithType.get(Tuple.of(startIndex, type));
    }

    public void memoize(int startIndex, TYPE type, AST<TYPE> to_memo, int length) {
        memoWithType.put(Tuple.of(startIndex, type), Tuple.of(to_memo, length));
    }
}
