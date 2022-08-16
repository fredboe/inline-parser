package org.parser.base;

import org.parser.Consumable;
import org.parser.base.build.ParserPool;
import org.parser.tree.AST;

import java.util.Optional;

public class PlaceholderParser<TYPE, ANNOTATION> implements Parser<TYPE, ANNOTATION> {
    private final ParserPool<TYPE, ANNOTATION> builder;
    private final String parserName;

    public PlaceholderParser(ParserPool<TYPE, ANNOTATION> builder, String parserName) {
        this.builder = builder;
        this.parserName = parserName;
    }

    @Override
    public Optional<AST<TYPE, ANNOTATION>> applyTo(Consumable consumable) {
        return builder.getParser(parserName).flatMap(parser -> parser.applyTo(consumable));
    }
}
