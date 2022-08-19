package org.parser.base;

import org.parser.Consumable;
import org.parser.tree.AST;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ManyParser<TYPE, ANNOTATION> implements Parser<TYPE, ANNOTATION> {
    private Parser<TYPE, ANNOTATION> parser;
    private final TYPE type;

    public ManyParser(TYPE type) {
        this.type = type;
        this.parser = null;
    }

    public ManyParser(TYPE type, Parser<TYPE, ANNOTATION> parser) {
        this.type = type;
        this.parser = parser;
    }

    @Override
    public Optional<AST<TYPE, ANNOTATION>> applyTo(Consumable consumable) {
        Optional<AST<TYPE, ANNOTATION>> optionalAST;
        List<AST<TYPE, ANNOTATION>> ASTs = new ArrayList<>();

        while ((optionalAST = parser.applyTo(consumable)).isPresent()) {
            ASTs.add(optionalAST.get());
        }
        return Optional.of(new AST<>(type, null, ASTs));
    }

    public void setParserIfNull(Parser<TYPE, ANNOTATION> parser) {
        if (this.parser == null) this.parser = parser;
    }
}
