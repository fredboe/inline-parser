package org.parser.base.build;

import org.parser.tree.AST;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public class Mode {
    public static <TYPE> Function<List<AST<TYPE>>, AST<TYPE>> childrenIfNoType(TYPE type) {
        return trees -> {
            List<AST<TYPE>> children = trees.stream()
                    .map(tree -> tree.getType() == null ? tree.getChildren() : List.of(tree))
                    .flatMap(Collection::stream).toList();
            return new AST<>(type, null, children);
        };
    }

    public static <TYPE> Function<List<AST<TYPE>>, AST<TYPE>> justFst() {
        return trees -> trees.size() >= 1
                ? trees.get(0)
                : new AST<TYPE>(null).setIgnore(true);
    }

    public static <TYPE> Function<List<AST<TYPE>>, AST<TYPE>> all(TYPE type) {
        return trees -> new AST<>(type, null, trees);
    }
}
