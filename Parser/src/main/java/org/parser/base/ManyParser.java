package org.parser.base;

import org.parser.base.build.Mode;
import org.parser.tree.AST;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * A many-parser holds a parser and executes it until it fails.
 * A many-parser is always successful, so it always returns an AST.
 * @param <TYPE> Type class of the AST.
 */
public class ManyParser<TYPE> implements Parser<TYPE> {
    private static final String errorMsg = "Fail at Many: Internal error. Should not happen.";
    /**
     * Parser to be executed repeatedly
     */
    private final Parser<TYPE> parser;
    /**
     * Delivers the resulting AST (at the moment always Mode.childrenIfNoType)
     */
    private final Function<List<AST<TYPE>>, AST<TYPE>> atSuccess;

    public ManyParser(TYPE type, Parser<TYPE> parser) {
        this.atSuccess = Mode.takeChildrenIfTypeNull(type);
        this.parser = parser;
    }

    /**
     * With a many-parser, the stored parser is executed until it fails.
     * At the end, an AST is then created, with the ASTs created by running the parser multiple times as the
     * children (so the children list can also be empty) and the stored type. If the type of the
     * AST is null, the AST is not taken as a child, but the children of the AST are taken as children.
     * are taken over.
     */
    @Override
    public void processWith(Environment<TYPE> environment) {
        executeParserRec(environment, 0);
    }

    private void executeParserRec(Environment<TYPE> environment, int n) {
        environment.executeAndThenCall(parser, (v) -> {
            assert !environment.resultStack().isEmpty() : errorMsg;

            if (environment.resultStack().peek().isPresent()) {
                executeParserRec(environment, n + 1);
            } else {
                environment.resultStack().pop();
                aggregateResults(environment, n);
            }
        });
    }

    private void aggregateResults(Environment<TYPE> environment, int n) {
        ArrayList<AST<TYPE>> ASTs = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            var optionalAST = environment.resultStack().pop();
            assert optionalAST.isPresent() : errorMsg;

            ASTs.add(0, optionalAST.get());
        }
        environment.resultStack().push(Optional.ofNullable(atSuccess.apply(ASTs)));
    }
}
