package org.parser.base;

import org.parser.Consumable;
import org.parser.tree.AST;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * Or-Parser
 */
public class OrParser<TYPE> implements DepthParser<TYPE> {
    private final List<Parser<TYPE>> subparsers;
    /**
     * This method is called as soon as the first parser was successful. It is then passed the supplied
     * AST is passed to it. This method should then eventually return the resulting AST.
     */
    private final Function<AST<TYPE>, AST<TYPE>> atSuccess;

    public OrParser(Function<AST<TYPE>, AST<TYPE>> atSuccess) {
        this.atSuccess = atSuccess != null ? atSuccess : Parser.basicOrAtSuccess();
        this.subparsers = new ArrayList<>();
    }

    public OrParser(Function<AST<TYPE>, AST<TYPE>> atSuccess,
                    List<Parser<TYPE>> subparsers) {
        this(atSuccess);
        if (subparsers != null) this.subparsers.addAll(subparsers);
    }

    /**
     * The method goes through all parsers and as soon as the first parser was successful on the consumable
     * was successful, the method atSuccess is called. Finally, the ignore bit is then set to the ignore bit * of the successful AST.
     * of the successful AST.
     * @param consumable Consumable
     * @return An AST wrapped with Optional (empty if all the parsers return an error)
     */
    @Override
    public Optional<AST<TYPE>> applyTo(Consumable consumable) {
        Optional<AST<TYPE>> optionalAST = subparsers.stream()
                .map(parser -> parser.applyTo(consumable))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();
        return optionalAST.map(ast -> atSuccess.apply(ast).setIgnore(ast.shouldIgnore()));
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

        if (other instanceof OrParser<?> parser) {
            return atSuccess.equals(parser.atSuccess) && subparsers.equals(parser.subparsers);
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(subparsers, atSuccess);
    }
}
