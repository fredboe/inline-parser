package org.parser.base;

import org.parser.Consumable;
import org.parser.tree.AST;

import java.util.Optional;

public class OptionalParser<TYPE> implements Parser<TYPE> {
    /**
     * Parser to be executed repeatedly
     */
    private final Parser<TYPE> parser;

    public OptionalParser(Parser<TYPE> parser) {
        this.parser = parser;
    }

    /**
     * With an optional-parser, the stored parser is executed once.
     * If the execution fails a shouldIgnore AST is returned, otherwise the ast of the execution is
     * returned. That means an optional-parser is always successful.
     * @param consumable Consumable
     * @return An AST wrapped with Optional (for Many this is always present).
     */
    @Override
    public Optional<AST<TYPE>> applyTo(Consumable consumable, Memoization<TYPE> memoization) {
       var optionalAST = parser.applyTo(consumable, memoization);
       return Optional.of(
               optionalAST.orElse(new AST<TYPE>(null).setIgnore(true))
       );
    }
}
