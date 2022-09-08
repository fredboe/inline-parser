package org.parser.base;

import org.parser.Consumable;
import org.parser.tree.AST;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * Or-Parser
 */
public class OrParser<TYPE> implements DepthParser<TYPE> {
    private List<Parser<TYPE>> parsers;
    /**
     * This method is called as soon as the first parser was successful. It is then passed the supplied
     * AST is passed to it. This method should then eventually return the resulting AST.
     */
    private final Function<AST<TYPE>, AST<TYPE>> atSuccess;

    public OrParser(Function<AST<TYPE>, AST<TYPE>> atSuccess) {
        this.atSuccess = atSuccess != null ? atSuccess : Parser.basicOrAtSuccess();
        this.parsers = new ArrayList<>();
    }

    public OrParser(Function<AST<TYPE>, AST<TYPE>> atSuccess,
                    List<Parser<TYPE>> parsers) {
        this(atSuccess);
        if (parsers != null) this.parsers = parsers;
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
        Optional<AST<TYPE>> optionalAST = parsers.stream()
                .map(parser -> parser.applyTo(consumable))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();
        return optionalAST.map(ast -> atSuccess.apply(ast).setIgnore(ast.shouldIgnore()));
    }

    @Override
    public void addSubparser(Parser<TYPE> subparser) {
        if (subparser != null) parsers.add(subparser);
    }

    @Override
    public boolean isEmpty() {
        return parsers.isEmpty();
    }
}
