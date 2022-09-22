package org.parser.base;

import org.parser.Consumable;
import org.parser.tree.AST;

import java.util.Optional;

public class PlaceholderParser<TYPE> implements Parser<TYPE> {
    private final String name;
    private Parser<TYPE> parser;

    public PlaceholderParser(String name) {
        this.name = name;
        this.parser = null;
    }

    @Override
    public Optional<AST<TYPE>> applyTo(Consumable consumable, Memoization<TYPE> memoization) {
        return Optional.ofNullable(parser).flatMap(parser -> parser.applyTo(consumable, memoization));
    }

    public String getName() {
        return name;
    }

    public void setParserIfNull(Parser<TYPE> parser) {
        if (this.parser == null) this.parser = parser;
    }
}
