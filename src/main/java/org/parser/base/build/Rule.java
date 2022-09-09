package org.parser.base.build;

import org.parser.base.ConcatParser;
import org.parser.base.OrParser;
import org.parser.base.Parser;
import org.parser.tree.AST;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;

public class Rule<TYPE> {
    private final String name;
    private final ParserBuilder<TYPE> parserBuilder;
    private final OrParser<TYPE> rule;
    private ConcatParser<TYPE> currentSubrule;
    private boolean frozen;

    public Rule(String name, ParserBuilder<TYPE> parserBuilder) {
        this.name = name;
        this.parserBuilder = parserBuilder;
        this.rule = Parser.or(new ArrayList<>());
        this.currentSubrule = null;
        this.frozen = false;
    }

    public String getName() {
        return name;
    }

    public Rule<TYPE> type(Function<List<AST<TYPE>>, AST<TYPE>> atSuccess) {
        newSubruleIfNotExists();
        currentSubrule.setAtSuccess(atSuccess);
        return this;
    }

    public Rule<TYPE> type(TYPE type) {
        return type(Mode.childrenIfNoType(type));
    }

    public Rule<TYPE> match(TYPE type, Pattern pattern) {
        return addToCurrentSubrule(Parser.match(type, pattern));
    }

    public Rule<TYPE> match(TYPE type, String regex) {
        return match(type, Pattern.compile(regex));
    }

    public Rule<TYPE> hide(Pattern pattern) {
        return addToCurrentSubrule(Parser.hide(pattern));
    }

    public Rule<TYPE> hide(String regex) {
        return hide(Pattern.compile(regex));
    }

    public Rule<TYPE> keyword(TYPE type, Pattern pattern) {
        return addToCurrentSubrule(Parser.keyword(type, pattern));
    }

    public Rule<TYPE> keyword(TYPE type, String regex) {
        return keyword(type, Pattern.compile(regex));
    }

    public Rule<TYPE> rule(String name) {
        return addToCurrentSubrule(parserBuilder.getPlaceholder(name));
    }

    public Rule<TYPE> many(TYPE type, String ruleName) {
        return addToCurrentSubrule(Parser.many(type, parserBuilder.getPlaceholder(ruleName)));
    }

    public Rule<TYPE> many(String ruleName) {
        return many(null, ruleName);
    }

    public Rule<TYPE> many(TYPE type, Simplerule<TYPE> simplerule) {
        return addToCurrentSubruleWithSubsubrule(rule -> Parser.many(type, rule), simplerule);
    }

    public Rule<TYPE> many(Simplerule<TYPE> simplerule) {
        return many(null, simplerule);
    }

    public Rule<TYPE> some(TYPE type, String ruleName) {
        return addToCurrentSubrule(Parser.some(type, parserBuilder.getPlaceholder(ruleName)));
    }

    public Rule<TYPE> some(String ruleName) {
        return some(null, ruleName);
    }

    public Rule<TYPE> some(TYPE type, Simplerule<TYPE> simplerule) {
        return addToCurrentSubruleWithSubsubrule(rule -> Parser.some(type, rule), simplerule);
    }

    public Rule<TYPE> some(Simplerule<TYPE> simplerule) {
        return some(null, simplerule);
    }

    public Rule<TYPE> optional(String ruleName) {
        return addToCurrentSubrule(Parser.optional(parserBuilder.getPlaceholder(ruleName)));
    }

    public Rule<TYPE> optional(Simplerule<TYPE> simplerule) {
        return addToCurrentSubruleWithSubsubrule(Parser::optional, simplerule);
    }

    public Rule<TYPE> subrule(Simplerule<TYPE> simplerule) {
        return addToCurrentSubruleWithSubsubrule(rule -> rule, simplerule);
    }

    private Rule<TYPE> addToCurrentSubruleWithSubsubrule(Function<ConcatParser<TYPE>, Parser<TYPE>> subruleMapper,
                                                         Simplerule<TYPE> subsubrule) {
        subsubrule.freeze();
        if (!frozen) parserBuilder.union(subsubrule.parserBuilder());
        return addToCurrentSubrule(subruleMapper.apply(subsubrule.parser()));
    }

    public Rule<TYPE> or() {
        if (!frozen && !currentSubrule.isEmpty()) {
            rule.addSubparser(currentSubrule);
            currentSubrule = null;
        }
        return this;
    }

    public void end() {
        if (!frozen) {
            freeze();
            parserBuilder.addParser(name, rule);
        }
    }

    public void freeze() {
        if (!frozen) {
            if (currentSubrule != null && !currentSubrule.isEmpty()) rule.addSubparser(currentSubrule);
            frozen = true;
        }
    }

    private Rule<TYPE> addToCurrentSubrule(Parser<TYPE> parser) {
        if (!frozen) {
            newSubruleIfNotExists();
            currentSubrule.addSubparser(parser);
        }
        return this;
    }

    private void newSubruleIfNotExists() {
        if (currentSubrule == null) {
            currentSubrule = new ConcatParser<>(Mode.justFst());
        }
    }
}

