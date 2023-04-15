package org.parser.base;

import org.parser.tree.AST;

import java.util.Optional;

public class OptionalParser<TYPE> implements Parser<TYPE> {
    /**
     * Parser to be executed repeatedly
     */
    private final Parser<TYPE> parser;

    public OptionalParser(Parser<TYPE> parser) {
        this.parser = parser;
    }

    /**
     * With an optional-parser, the stored parser is executed once.
     * If the execution fails a shouldIgnore AST is returned, otherwise the ast of the execution is
     * returned. That means an optional-parser is always successful.
     */
    @Override
    public void processWith(Environment<TYPE> environment) {
        environment.executeAndThenCall(parser, (v) -> {
            var optionalAST = environment.resultStack().pop();
            environment.resultStack().push(
                    Optional.of(optionalAST.orElse(new AST<TYPE>(null).setIgnore(true)))
            );
        });
    }
}
