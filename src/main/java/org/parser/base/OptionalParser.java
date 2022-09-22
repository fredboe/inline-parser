package org.parser.base;

import org.parser.Consumable;
import org.parser.tree.AST;

import java.util.Optional;

public class OptionalParser<TYPE> implements SingleParser<TYPE> {
    /**
     * Parser to be executed repeatedly
     */
    private final Parser<TYPE> subparser;

    public OptionalParser(Parser<TYPE> subparser) {
        this.subparser = subparser;
    }

    /**
     * With an optional-parser, the stored parser is executed once.
     * If the execution fails a shouldIgnore AST is returned, otherwise the ast of the execution is
     * returned. That means an optional-parser is always successful.
     * @param consumable Consumable
     * @return An AST wrapped with Optional (for Many this is always present).
     */
    @Override
    public Optional<AST<TYPE>> applyTo(Consumable consumable) {
       var optionalAST = subparser.applyTo(consumable);
       return Optional.of(
               optionalAST.orElse(new AST<TYPE>(null).setIgnore(true))
       );
    }

    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null) return false;

        if (other instanceof OptionalParser<?> parser) {
            return  this.subparser.equals(parser.subparser);
        }
        return false;
    }

    public int hashCode() {
        return subparser.hashCode();
    }
}
