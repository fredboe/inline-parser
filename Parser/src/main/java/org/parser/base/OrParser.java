package org.parser.base;

import org.parser.tree.AST;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * Or-Parser
 */
public class OrParser<TYPE> implements WithSubparsers<TYPE> {
    private static final String errorMsg = "Fail at Or: Internal error. Should not happen.";
    private final List<Parser<TYPE>> parsers;
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
        if (parsers != null) this.parsers.addAll(parsers);
    }

    @Override
    public void processWith(Environment<TYPE> environment) {
        processParsersJustAtFailureRec(environment, 0);
    }

    private void processParsersJustAtFailureRec(Environment<TYPE> environment, int index) {
        if (index < parsers.size()) {
            var parser = parsers.get(index);
            environment.executeAndThenCall(parser, (v) -> {
                assert !environment.resultStack().isEmpty() : errorMsg;

                var optionalAST = environment.resultStack().pop();
                if (optionalAST.isEmpty()) {
                    processParsersJustAtFailureRec(environment, index + 1);
                } else {
                    var ast = optionalAST.get();
                    environment.resultStack().push(Optional.of(atSuccess.apply(ast).setIgnore(ast.shouldIgnore())));
                }
            });
        } else {
            environment.resultStack().push(Optional.empty());
        }
    }

    @Override
    public void addSubparser(Parser<TYPE> subparser) {
        if (subparser != null) parsers.add(subparser);
    }

    @Override
    public boolean isEmpty() {
        return parsers.isEmpty();
    }

    @Override
    public int size() {
        return parsers.size();
    }


}
