package org.parser.base;

import org.parser.Consumable;
import org.parser.tree.AST;

import java.util.Optional;

public class PlaceholderParser<TYPE> implements Parser<TYPE> {
    private Parser<TYPE> subparser;

    public PlaceholderParser() {
        this.subparser = null;
    }

    @Override
    public Optional<AST<TYPE>> applyTo(Consumable consumable) {
        return Optional.ofNullable(subparser).flatMap(parser -> parser.applyTo(consumable));
    }

    public void setParserIfNull(Parser<TYPE> parser) {
        if (this.subparser == null) this.subparser = parser;
    }

    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null) return false;

        if (other instanceof PlaceholderParser<?> parser) {
            return  this.subparser.equals(parser.subparser);
        }
        return false;
    }

    public int hashCode() {
        return subparser.hashCode();
    }
}
