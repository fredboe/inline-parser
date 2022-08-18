package org.parser.base;

import org.parser.Consumable;
import org.parser.tree.AST;

import java.util.Optional;

public class PlaceholderParser<TYPE, ANNOTATION> implements Parser<TYPE, ANNOTATION> {
    private Parser<TYPE, ANNOTATION> parser;

    public PlaceholderParser() {
        this.parser = null;
    }

    @Override
    public Optional<AST<TYPE, ANNOTATION>> applyTo(Consumable consumable) {
        return Optional.ofNullable(parser).flatMap(parser -> parser.applyTo(consumable));
    }

    public void setParserIfNull(Parser<TYPE, ANNOTATION> parser) {
        if (this.parser == null) this.parser = parser;
    }
}
