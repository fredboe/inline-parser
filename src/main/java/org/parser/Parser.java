package org.parser;

import org.parser.base.ConcatParser;
import org.parser.base.OrParser;
import org.parser.base.RegExParser;
import org.parser.tree.AST;

import java.util.Optional;
import java.util.regex.Pattern;

/**
 * If a parser was successful is determined by the match object of the returned AST or when its null.
 * Später richtiges Fail und so implementieren. -> Ist bis jetzt noch falsch
 *
 * null checks erweitern
 */

/**
 * Ein Parser sollte nur die consumable konsumieren, wenn der Parser erfolgreich ist.
 * @param <TYPE> Typ
 * @param <ANNOTATION> ANNOTATION-Class bei Abstract Syntax Tree
 */
public interface Parser<TYPE, ANNOTATION> {
    Optional<AST<TYPE, ANNOTATION>> applyTo(Consumable consumable);

    @SafeVarargs
    static <TYPE, ANNOTATION> Parser<TYPE, ANNOTATION> or(TYPE type, Parser<TYPE, ANNOTATION> ... parsers) {
        return new OrParser<>(ast -> {
            AST<TYPE, ANNOTATION> result = new AST<>(type);
            result.addChild(ast);
            if (ast.shouldIgnore()) result.ignore();
            return result;
        } , parsers);
    }

    @SafeVarargs
    static <TYPE, ANNOTATION> Parser<TYPE, ANNOTATION> concat(TYPE type, Parser<TYPE, ANNOTATION> ... parsers) {
        return new ConcatParser<>(trees -> new AST<>(type, null, trees), parsers);
    }

    static <TYPE, ANNOTATIONS> Parser<TYPE, ANNOTATIONS> hide(TYPE type, Pattern pattern) {
        return new RegExParser<>(pattern, match -> new AST<TYPE, ANNOTATIONS>(type).ignore());
    }

    static <TYPE, ANNOTATIONS> Parser<TYPE, ANNOTATIONS> keyword(TYPE type, Pattern pattern) {
        return new RegExParser<>(pattern, match -> new AST<>(type, null));
    }

    static <TYPE, ANNOTATIONS> Parser<TYPE, ANNOTATIONS> match(TYPE type, Pattern pattern) {
        return new RegExParser<>(pattern, match -> new AST<>(type, match));
    }
}
