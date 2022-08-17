package org.parser.base.build;

import org.parser.Consumable;
import org.parser.base.OrParser;
import org.parser.base.Parser;
import org.parser.base.RegExParser;
import org.parser.tree.AST;

import java.util.ArrayList;
import java.util.function.Function;
import java.util.regex.Pattern;

// null checks
// Patterns und atSuccess hinzufügen
public class RuleBuilder<TYPE, ANNOTATION> {
    private final String ruleName;
    private final ParserBuilder<TYPE, ANNOTATION> parserBuilder;
    private final OrParser<TYPE, ANNOTATION> rule;
    private boolean frozen;

    private final NextIsOrBuilder<TYPE, ANNOTATION> nextIsOrBuilder;
    private final ConcatRuleBuilder<TYPE, ANNOTATION> concatRuleBuilder;

    public RuleBuilder(String ruleName, ParserBuilder<TYPE, ANNOTATION> parserBuilder) {
        this.ruleName = ruleName;
        this.parserBuilder = parserBuilder;
        this.rule = Parser.or(new ArrayList<>());
        this.nextIsOrBuilder = new NextIsOrBuilder<>(parserBuilder, this);
        this.concatRuleBuilder = new ConcatRuleBuilder<>(parserBuilder, this);
        this.frozen = false;
    }

    public ConcatRuleBuilder<TYPE, ANNOTATION> concat() {
        concatRuleBuilder.newSubrule(trees ->
                trees.size() >= 1
                ? trees.get(0)
                : null
        );
        return concatRuleBuilder;
    }

    public ConcatRuleBuilder<TYPE, ANNOTATION> concat(TYPE type) {
        concatRuleBuilder.newSubrule(type);
        return concatRuleBuilder;
    }

    public NextIsOrBuilder<TYPE, ANNOTATION> match(TYPE type, String regex) {
        return addSingleClause(Parser.match(type, regex));
    }

    public NextIsOrBuilder<TYPE, ANNOTATION> keyword(TYPE type, String regex) {
        return addSingleClause(Parser.keyword(type, regex));
    }

    public NextIsOrBuilder<TYPE, ANNOTATION> customRegEx(Function<Consumable.Match, AST<TYPE, ANNOTATION>> atSuccess,
                                                         String regex) {
        if (regex == null) regex = "";
        return addSingleClause(new RegExParser<>(Pattern.compile(regex), atSuccess));
    }

    public NextIsOrBuilder<TYPE, ANNOTATION> rule(String name) {
        return addSingleClause(parserBuilder.getPlaceholder(name));
    }

    public String getName() {
        return ruleName;
    }

    private NextIsOrBuilder<TYPE, ANNOTATION> addSingleClause(Parser<TYPE, ANNOTATION> singleParser) {
        addClause(singleParser);
        return nextIsOrBuilder;
    }

    void addClause(Parser<TYPE, ANNOTATION> parser) {
        if (!frozen) rule.addSubparser(parser);
    }

    Parser<TYPE, ANNOTATION> freeze() {
        frozen = true;
        return rule;
    }
}
