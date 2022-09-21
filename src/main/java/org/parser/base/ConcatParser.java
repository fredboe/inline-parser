package org.parser.base;

import org.parser.Consumable;
import org.parser.tree.AST;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * Concatenation parser
 * Rules:
 * - The atSuccess function must in any case also consider the case when the passed list is empty.
 */
public class ConcatParser<TYPE> implements DepthParser<TYPE> {
    /**
     * set of parsers to be added one after the other (order is important)
     */
    private List<Parser<TYPE>> subparsers;
    /**
     * This function is called when all parsers in the parser list have returned a successful AST
     * have been delivered. The list of supplied ASTs (without the ignored ASTs) is then passed to this method.
     *. This method should then eventually return the resulting AST.
     */
    private Function<List<AST<TYPE>>, AST<TYPE>> atSuccess;

    public ConcatParser(Function<List<AST<TYPE>>, AST<TYPE>> atSuccess) {
        this.atSuccess = atSuccess != null ? atSuccess : Parser.basicConcatAtSuccess(null);
        this.subparsers = new ArrayList<>();
    }

    public ConcatParser(Function<List<AST<TYPE>>, AST<TYPE>> atSuccess,
                        List<Parser<TYPE>> subparsers) {
        this(atSuccess);
        if (subparsers != null) this.subparsers = subparsers;
    }

    public void setAtSuccess(Function<List<AST<TYPE>>, AST<TYPE>> atSuccess) {
        this.atSuccess = atSuccess;
    }

    /**
     * Applies all parsers to the consumable object in sequence. The method returns only a successful
     * AST if all parsers were successful. AtSuccess is then called for the resulting AST.
     * @param consumable Consumable
     * @return An AST wrapped with Optional (empty if one of the parsers returns an error).
     */
    @Override
    public Optional<AST<TYPE>> applyTo(Consumable consumable) {
        Consumable copy = new Consumable(consumable); // in case of failure nothing should be consumed
        List<AST<TYPE>> ASTrees = new ArrayList<>(subparsers.size());
        for (Parser<TYPE> parser : subparsers) {
            Optional<AST<TYPE>> tree = parser.applyTo(consumable);
            if (tree.isEmpty()) {
                consumable.resetTo(copy);
                return Optional.empty();
            }
            if (!tree.get().shouldIgnore()) ASTrees.add(tree.get());
        }
        return Optional.ofNullable(atSuccess.apply(ASTrees));
    }

    @Override
    public void addSubparser(Parser<TYPE> subparser) {
        if (subparser != null) subparsers.add(subparser);
    }

    @Override
    public boolean isEmpty() {
        return subparsers.isEmpty();
    }

    @Override
    public int size() {
        return subparsers.size();
    }

    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null) return false;

        if (other instanceof ConcatParser<?> parser) {
            return atSuccess.equals(parser.atSuccess) && subparsers.equals(parser.subparsers);
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(subparsers, atSuccess);
    }
}