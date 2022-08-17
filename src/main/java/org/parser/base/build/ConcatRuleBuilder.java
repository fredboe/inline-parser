package org.parser.base.build;

import org.parser.base.ConcatParser;
import org.parser.base.Parser;
import org.parser.tree.AST;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ConcatRuleBuilder<TYPE, ANNOTATION> {
    private final ParserBuilder<TYPE, ANNOTATION> parserBuilder;
    private final RuleBuilder<TYPE, ANNOTATION> ruleBuilder;
    private ConcatParser<TYPE, ANNOTATION> subrule;
    private boolean frozen;

    public ConcatRuleBuilder(ParserBuilder<TYPE, ANNOTATION> parserBuilder, RuleBuilder<TYPE, ANNOTATION> ruleBuilder) {
        if (parserBuilder == null || ruleBuilder == null)
            throw new RuntimeException("Parser-Builder und Rule-Builder dürfen nicht null sein");
        this.parserBuilder = parserBuilder;
        this.ruleBuilder = ruleBuilder;
        this.subrule = null;
        this.frozen = true;
    }

    void newSubrule(Function<List<AST<TYPE, ANNOTATION>>, AST<TYPE, ANNOTATION>> atSuccess) {
        if (frozen) {
            this.subrule = new ConcatParser<>(atSuccess);
            frozen = false;
        }
    }

    void newSubrule(TYPE type) {
        newSubrule(Parser.basicConcatAtSuccess(type));
    }

    public ConcatRuleBuilder<TYPE, ANNOTATION> match(TYPE type, String regex) {
        return addStep(Parser.match(type, regex));
    }

    public ConcatRuleBuilder<TYPE, ANNOTATION> keyword(TYPE type, String regex) {
        return addStep(Parser.keyword(type, regex));
    }

    public ConcatRuleBuilder<TYPE, ANNOTATION> hide(String regex) {
        return addStep(Parser.hide(regex));
    }

    public ConcatRuleBuilder<TYPE, ANNOTATION> rule(String name) {
        return addStep(parserBuilder.getPlaceholder(name));
    }

    public RuleBuilder<TYPE, ANNOTATION> or() {
        frozen = true;
        ruleBuilder.addClause(subrule);
        return ruleBuilder;
    }

    public void end() {
        if (!subrule.isEmpty()) ruleBuilder.addClause(subrule);
        frozen = true;
        parserBuilder.addParser(ruleBuilder.getName(), ruleBuilder.freeze());
    }

    private ConcatRuleBuilder<TYPE, ANNOTATION> addStep(Parser<TYPE, ANNOTATION> parser) {
        if (!frozen) subrule.addSubparser(parser);
        return this;
    }
}
