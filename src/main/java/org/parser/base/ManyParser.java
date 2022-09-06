package org.parser.base;

import org.parser.Consumable;
import org.parser.tree.AST;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * A many-parser holds a parser and executes it until it fails.
 * A many-parser is always successful, so it always returns an AST.
 * @param <TYPE> Type class of the AST.
 * @param <ANNOTATION> annotation class of the AST.
 */
public class ManyParser<TYPE, ANNOTATION> implements Parser<TYPE, ANNOTATION> {
    /**
     * Type of an AST created with Many
     */
    private final TYPE type;
    /**
     * Parser to be executed repeatedly
     */
    private Parser<TYPE, ANNOTATION> parser;

    public ManyParser(TYPE type) {
        this.type = type;
        this.parser = null;
    }

    public ManyParser(TYPE type, Parser<TYPE, ANNOTATION> parser) {
        this.type = type;
        this.parser = parser;
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
    public Optional<AST<TYPE, ANNOTATION>> applyTo(Consumable consumable) {
        Optional<AST<TYPE, ANNOTATION>> optionalAST;
        List<AST<TYPE, ANNOTATION>> ASTs = new ArrayList<>();

        while ((optionalAST = parser.applyTo(consumable)).isPresent()) {
            var ast = optionalAST.get();
            if (!ast.shouldIgnore()) {
                if (ast.getType() != null) {
                    ASTs.add(optionalAST.get());
                } else {
                    ASTs.addAll(ast.getChildren());
                }
            }
        }
        return Optional.of(new AST<>(type, null, ASTs));
    }

    public void setParserIfNull(Parser<TYPE, ANNOTATION> parser) {
        if (this.parser == null) this.parser = parser;
    }
}
