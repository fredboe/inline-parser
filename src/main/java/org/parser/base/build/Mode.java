package org.parser.base.build;

import org.parser.tree.AST;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

/**
 * This class contains some atSuccess-Method one can use for the concat-parser.
 */
public class Mode {
    /**
     *
     * @param type Type of the resulting AST.
     * @return Returns the atSuccess-Method that generates an AST with the given type. The children
     *         of the AST are formed as follows: If a given AST has a type other than null then this AST
     *         is just taken over as a child, otherwise the children of the given AST are added to the children-list.
     */
    public static <TYPE> Function<List<AST<TYPE>>, AST<TYPE>> childrenIfNoType(TYPE type) {
        return trees -> {
            List<AST<TYPE>> children = trees.stream()
                    .map(tree -> tree.getType() == null ? tree.getChildren() : List.of(tree))
                    .flatMap(Collection::stream).toList();
            return new AST<>(type, null, children);
        };
    }

    /**
     *
     * @return Returns the atSuccess-Method that just returns the first given AST. If there are no children
     *         then a "to ignore" AST is returned.
     */
    public static <TYPE> Function<List<AST<TYPE>>, AST<TYPE>> justFst() {
        return trees -> trees.size() >= 1
                ? trees.get(0)
                : new AST<TYPE>(null).setIgnore(true);
    }

    /**
     *
     * @param type Type of the resulting AST.
     * @return Returns an AST of the given type with all given ASTs as children.
     */
    public static <TYPE> Function<List<AST<TYPE>>, AST<TYPE>> all(TYPE type) {
        return trees -> new AST<>(type, null, trees);
    }
}
