package org.parser.base.build;

import org.parser.base.ConcatParser;
import org.parser.base.OrParser;
import org.parser.base.Parser;
import org.parser.base.PlaceholderParser;

import java.util.ArrayList;

// concat ohne typ, or mit typ, pattern funktionen einfügen, atSuccess Option einfügen
// evtl umwandlung der PlaceholderParser zu anderen parsern (dann ergibt auch build einen sinn)
public class RuleBuilder<TYPE, ANNOTATION> {
    private final String name;
    private final ParserPool<TYPE, ANNOTATION> parserBuilder;
    private final OrParser<TYPE, ANNOTATION> rule;
    private ConcatParser<TYPE, ANNOTATION> currentSubrule;

    private boolean editable;

    public RuleBuilder(ParserPool<TYPE, ANNOTATION> parserBuilder, String name) {
        this.parserBuilder = parserBuilder;
        this.name = name;
        this.rule = Parser.or(new ArrayList<>());
        this.currentSubrule = null;
        this.editable = true;
    }

    public RuleBuilder<TYPE, ANNOTATION> concat(TYPE type) {
        if (!editable) return this;
        genNewSubrule(type);
        return this;
    }

    public RuleBuilder<TYPE, ANNOTATION> match(TYPE type, String regex) {
        return addToConcat(Parser.match(type, regex));
    }

    public RuleBuilder<TYPE, ANNOTATION> hide(String regex) {
        return addToConcat(Parser.hide(regex));
    }

    public RuleBuilder<TYPE, ANNOTATION> keyword(TYPE type, String regex) {
        return addToConcat(Parser.keyword(type, regex));
    }

    public RuleBuilder<TYPE, ANNOTATION> rule(String name) {
        return addToConcat(new PlaceholderParser<>(parserBuilder, name));
    }

    public RuleBuilder<TYPE, ANNOTATION> or() {
        addClauseAndErase();
        currentSubrule = null;
        return this;
    }

    public Parser<TYPE, ANNOTATION> end() {
        addClauseAndErase();
        editable = false;
        parserBuilder.addParser(name, rule);
        return rule;
    }

    private void addClauseAndErase() {
        if (currentSubrule != null) {
            rule.addSubparser(currentSubrule);
            currentSubrule = null;
        }
    }

    private RuleBuilder<TYPE, ANNOTATION> addToConcat(Parser<TYPE, ANNOTATION> parser) {
        if (!editable) return this;
        if (currentSubrule == null) genNewSubrule(null);
        currentSubrule.addSubparser(parser);
        return this;
    }

    private void genNewSubrule(TYPE type) {
        currentSubrule = Parser.concat(type, new ArrayList<>());
    }
}
