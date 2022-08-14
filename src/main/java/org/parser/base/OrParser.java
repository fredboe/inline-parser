package org.parser.base;

import org.parser.Consumable;
import org.parser.Parser;
import org.parser.Utils;
import org.parser.tree.AST;

import java.util.Optional;
import java.util.function.Function;

public class OrParser<TYPE, ANNOTATION> implements Parser<TYPE, ANNOTATION> {
    private final Parser<TYPE, ANNOTATION>[] parsers;
    private final Function<AST<TYPE, ANNOTATION>, AST<TYPE, ANNOTATION>> atSuccess;

    @SafeVarargs
    public OrParser(Function<AST<TYPE, ANNOTATION>, AST<TYPE, ANNOTATION>> atSuccess,
                    Parser<TYPE, ANNOTATION> ... parsers) {
        this.atSuccess = atSuccess;
        this.parsers = parsers;
    }

    @Override
    public Optional<AST<TYPE, ANNOTATION>> applyTo(Consumable consumable) {
        for (Parser<TYPE, ANNOTATION> parser : parsers) {
            Optional<AST<TYPE, ANNOTATION>> ast = parser.applyTo(consumable);
            if (ast.isPresent()) {
                return Utils.convertToOptional(atSuccess.apply(ast.get()));
            }
        }
        return Optional.empty();
    }
}
