package org.parser.base;

import org.parser.Consumable;
import org.parser.base.build.Mode;
import org.parser.tree.AST;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * A many-parser holds a parser and executes it until it fails.
 * A many-parser is always successful, so it always returns an AST.
 * @param <TYPE> Type class of the AST.
 */
public class ManyParser<TYPE> implements Parser<TYPE> {
    /**
     * Type of AST created with Many
     */
    private final TYPE type;
    /**
     * Parser to be executed repeatedly
     */
    private final Parser<TYPE> subparser;

    public ManyParser(TYPE type, Parser<TYPE> subparser) {
        this.type = type;
        this.subparser = subparser;
    }

    /**
     * With a many-parser, the stored parser is executed until it fails.
     * At the end, an AST is then created, with the ASTs created by running the parser multiple times as the
     * children (so the children list can also be empty) and the stored type. If the type of the
     * AST is null, the AST is not taken as a child, but the children of the AST are taken as children.
     * are taken over.
     * @param consumable Consumable
     * @return An AST wrapped with Optional (for Many this is always present).
     */
    @Override
    public Optional<AST<TYPE>> applyTo(Consumable consumable) {
        Optional<AST<TYPE>> optionalAST;
        List<AST<TYPE>> ASTs = new ArrayList<>();

        while ((optionalAST = subparser.applyTo(consumable)).isPresent()) {
            var ast = optionalAST.get();
            if (!ast.shouldIgnore()) ASTs.add(ast);
        }
        return Optional.of(Mode.childrenIfNoType(type).apply(ASTs));
    }

    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null) return false;

        if (other instanceof ManyParser<?> parser) {
            return type.equals(parser.type) && this.subparser.equals(parser.subparser);
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(type, subparser);
    }
}
