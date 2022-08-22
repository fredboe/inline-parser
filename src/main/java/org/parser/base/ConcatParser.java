package org.parser.base;

import org.parser.Consumable;
import org.parser.tree.AST;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * Concatenation parser
 * Rules:
 * - The atSuccess function must in any case also consider the case when the passed list is empty.
 */
public class ConcatParser<TYPE, ANNOTATION> implements DepthParser<TYPE, ANNOTATION> {
    /**
     * set of parsers to be added one after the other (order is important)
     */
    private List<Parser<TYPE, ANNOTATION>> parsers;
    /**
     * This function is called when all parsers in the parser list have returned a successful AST
     * have been delivered. The list of supplied ASTs (without the ignored ASTs) is then passed to this method.
     *. This method should then eventually return the resulting AST.
     */
    private final Function<List<AST<TYPE, ANNOTATION>>, AST<TYPE, ANNOTATION>> atSuccess;

    public ConcatParser(Function<List<AST<TYPE, ANNOTATION>>, AST<TYPE, ANNOTATION>> atSuccess) {
        this.atSuccess = atSuccess != null ? atSuccess : Parser.basicConcatAtSuccess(null);
        this.parsers = new ArrayList<>();
    }

    public ConcatParser(Function<List<AST<TYPE, ANNOTATION>>, AST<TYPE, ANNOTATION>> atSuccess,
                        List<Parser<TYPE, ANNOTATION>> parsers) {
        this(atSuccess);
        if (parsers != null) this.parsers = parsers;
    }

    /**
     * Applies all parsers to the consumable object in sequence. The method returns only a successful
     * AST if all parsers were successful. AtSuccess is then called for the resulting AST.
     * @param consumable Consumable
     * @return An AST wrapped with Optional (empty if one of the parsers returns an error).
     */
    @Override
    public Optional<AST<TYPE, ANNOTATION>> applyTo(Consumable consumable) {
        Consumable copy = new Consumable(consumable); // in case of failure nothing should be consumed
        List<AST<TYPE, ANNOTATION>> ASTrees = new ArrayList<>(parsers.size());
        for (Parser<TYPE, ANNOTATION> parser : parsers) {
            Optional<AST<TYPE, ANNOTATION>> tree = parser.applyTo(consumable);
            if (tree.isEmpty()) {
                consumable.resetTo(copy);
                return Optional.empty();
            }
            if (!tree.get().shouldIgnore()) ASTrees.add(tree.get());
        }
        return Optional.ofNullable(atSuccess.apply(ASTrees));
    }

    @Override
    public void addSubparser(Parser<TYPE, ANNOTATION> subparser) {
        if (subparser != null) parsers.add(subparser);
    }

    @Override
    public boolean isEmpty() {
        return parsers.isEmpty();
    }
}