package org.parser.base;

import org.parser.Consumable;
import org.parser.tree.AST;

import java.util.Optional;

public class PlaceholderParser<TYPE> implements Parser<TYPE> {
    private Parser<TYPE> parser;

    public PlaceholderParser() {
        this.parser = null;
    }

    @Override
    public Optional<AST<TYPE>> applyTo(Consumable consumable) {
        return Optional.ofNullable(parser).flatMap(parser -> parser.applyTo(consumable));
    }

    public void setParserIfNull(Parser<TYPE> parser) {
        if (this.parser == null) this.parser = parser;
    }
}
