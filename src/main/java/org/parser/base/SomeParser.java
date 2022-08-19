package org.parser.base;

import org.parser.Consumable;
import org.parser.tree.AST;

import java.util.Optional;

public class SomeParser<TYPE, ANNOTATION> implements Parser<TYPE, ANNOTATION> {
    private final TYPE type;
    private Parser<TYPE, ANNOTATION> parser = null;

    public SomeParser(TYPE type) {
        this.type = type;
    }

    public SomeParser(TYPE type, Parser<TYPE, ANNOTATION> parser) {
        this.type = type;
        this.parser = parser;
    }

    @Override
    public Optional<AST<TYPE, ANNOTATION>> applyTo(Consumable consumable) {
        ConcatParser<TYPE, ANNOTATION> someParser = new ConcatParser<>(trees -> trees.get(1).addChild(trees.get(0)));
        someParser.addSubparser(parser);
        someParser.addSubparser(new ManyParser<>(type, parser));
        return someParser.applyTo(consumable);
    }

    public void setParserIfNull(Parser<TYPE, ANNOTATION> parser) {
        if (this.parser == null) this.parser = parser;
    }
}
