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
public class ConcatParser<TYPE> implements WithSubparsers<TYPE> {
    /**
     * set of parsers to be added one after the other (order is important)
     */
    private final List<Parser<TYPE>> parsers;
    /**
     * This function is called when all parsers in the parser list have returned a successful AST
     * have been delivered. The list of supplied ASTs (without the ignored ASTs) is then passed to this method.
     *. This method should then eventually return the resulting AST.
     */
    private Function<List<AST<TYPE>>, AST<TYPE>> atSuccess;

    public ConcatParser(Function<List<AST<TYPE>>, AST<TYPE>> atSuccess) {
        this.atSuccess = atSuccess != null ? atSuccess : Parser.basicConcatAtSuccess(null);
        this.parsers = new ArrayList<>();
    }

    public ConcatParser(Function<List<AST<TYPE>>, AST<TYPE>> atSuccess,
                        List<Parser<TYPE>> parsers) {
        this(atSuccess);
        if (parsers != null) this.parsers.addAll(parsers);
    }

    public void setAtSuccess(Function<List<AST<TYPE>>, AST<TYPE>> atSuccess) {
        this.atSuccess = atSuccess;
    }

    @Override
    public void processWith(Environment<TYPE> environment) {
        processParsersRec(environment, 0, environment.createConsumableMark());
    }

    private void processParsersRec(Environment<TYPE> environment, int index, Consumable.Mark mark) {
        if (index == parsers.size()) {
            aggregateResults(environment);
        } else {
            var parser = parsers.get(index);
            environment.executeAndThenCall(parser, (consumable) -> {
                assert !environment.resultStack().isEmpty() : "Fail at Concat: parser should have pushed a result.";

                if (environment.resultStack().peek().isPresent()) {
                    processParsersRec(environment, index + 1, mark);
                } else {
                    clearWhenFailed(environment, index, consumable, mark);
                    environment.resultStack().push(Optional.empty());
                }
            });
        }
    }

    private void aggregateResults(Environment<TYPE> environment) {
        ArrayList<AST<TYPE>> ASTs = new ArrayList<>(parsers.size());
        for (int i = 0; i < parsers.size(); i++) {
            // push fail if get fails
            var ast = environment.resultStack().pop().get();
            if (!ast.shouldIgnore()) ASTs.add(0, ast);
        }
        environment.resultStack().push(Optional.of(atSuccess.apply(ASTs)));
    }

    private void clearWhenFailed(Environment<TYPE> environment, int failIndex, Consumable consumable, Consumable.Mark mark) {
        for (int i = 0; i < failIndex + 1; i++) {
            environment.resultStack().pop();
        }
        consumable.goBackToMark(mark);
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