package org.parser.base;

import org.parser.Consumable;
import org.parser.base.build.ParserBuilder;
import org.parser.tree.AST;

import java.util.Optional;

public class PlaceholderParser<TYPE, ANNOTATION> implements Parser<TYPE, ANNOTATION> {
    private final ParserBuilder<TYPE, ANNOTATION> builder;
    private final String parserName;

    public PlaceholderParser(ParserBuilder<TYPE, ANNOTATION> builder, String parserName) {
        this.builder = builder;
        this.parserName = parserName;
    }

    @Override
    public Optional<AST<TYPE, ANNOTATION>> applyTo(Consumable consumable) {
        return builder.getParser(parserName).flatMap(parser -> parser.applyTo(consumable));
    }
}
