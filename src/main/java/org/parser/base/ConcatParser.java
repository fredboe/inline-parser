package org.parser.base;

import org.parser.Consumable;
import org.parser.Parser;
import org.parser.Utils;
import org.parser.tree.AST;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

// empty list means every sub-parser has been an ignore-parser
/**
 * Should generate a copy of the consumable if the parser fails
 */
public class ConcatParser<TYPE, ANNOTATION> implements Parser<TYPE, ANNOTATION> {
    private final List<Parser<TYPE, ANNOTATION>> parsers;
    private final Function<List<AST<TYPE, ANNOTATION>>, AST<TYPE, ANNOTATION>> atSuccess;

    @SafeVarargs
    public ConcatParser(Function<List<AST<TYPE, ANNOTATION>>, AST<TYPE, ANNOTATION>> atSuccess,
                        Parser<TYPE, ANNOTATION> ... parsers) {
        this.atSuccess = atSuccess;
        this.parsers = Arrays.asList(parsers);
    }

    @Override
    public Optional<AST<TYPE, ANNOTATION>> applyTo(Consumable consumable) {
        Consumable copy = new Consumable(consumable);
        List<AST<TYPE, ANNOTATION>> ASTrees = new ArrayList<>(parsers.size());
        for (Parser<TYPE, ANNOTATION> parser : parsers) {
            Optional<AST<TYPE, ANNOTATION>> tree = parser.applyTo(consumable);
            if (tree.isEmpty()) {
                consumable.resetTo(copy);
                return Optional.empty();
            }
            if (!tree.get().shouldIgnore()) ASTrees.add(tree.get());
        }
        return Utils.convertToOptional(atSuccess.apply(ASTrees));
    }
}