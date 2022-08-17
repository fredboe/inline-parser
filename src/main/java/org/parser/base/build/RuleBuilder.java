package org.parser.base.build;

import org.parser.Consumable;
import org.parser.base.*;
import org.parser.tree.AST;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;

// concat ohne typ, or mit typ, pattern funktionen einfügen, atSuccess Option einfügen
// evtl umwandlung der PlaceholderParser zu anderen parsern (dann ergibt auch build einen sinn)
// testen vor allem rule
// operator parser
public class RuleBuilder<TYPE, ANNOTATION> {
    private final String name;
    private final ParserPool<TYPE, ANNOTATION> parserBuilder;
    private final OrParser<TYPE, ANNOTATION> rule;
    private ConcatParser<TYPE, ANNOTATION> currentSubrule;
    private boolean editable;

    public RuleBuilder(ParserPool<TYPE, ANNOTATION> parserBuilder, String name,
                       Function<AST<TYPE, ANNOTATION>, AST<TYPE, ANNOTATION>> atSuccessOr) {
        this.parserBuilder = parserBuilder;
        this.name = name;
        this.rule = new OrParser<>(atSuccessOr, new ArrayList<>());
        this.currentSubrule = null;
        this.editable = true;
    }

    public RuleBuilder(ParserPool<TYPE, ANNOTATION> parserBuilder, String name, TYPE type) {
        this(parserBuilder, name, ast -> ast.getType() == null
                ? new AST<>(type, null, ast.getChildren())
                : ast
        );
    }

    public RuleBuilder<TYPE, ANNOTATION> concat(Function<List<AST<TYPE, ANNOTATION>>, AST<TYPE, ANNOTATION>> atSuccess) {
        if (currentSubrule == null) genNewSubrule(atSuccess);
        return this;
    }

    public RuleBuilder<TYPE, ANNOTATION> customRegEx(Pattern pattern, Function<Consumable.Match, AST<TYPE, ANNOTATION>> atSuccess) {
        return addToConcat(new RegExParser<>(pattern, atSuccess));
    }

    public RuleBuilder<TYPE, ANNOTATION> match(TYPE type, Pattern pattern) {
        return addToConcat(Parser.match(type, pattern));
    }

    public RuleBuilder<TYPE, ANNOTATION> match(TYPE type, String regex) {
        return match(type, Pattern.compile(regex));
    }

    public RuleBuilder<TYPE, ANNOTATION> hide(Pattern pattern) {
        return addToConcat(Parser.hide(pattern));
    }

    public RuleBuilder<TYPE, ANNOTATION> hide(String regex) {
        return hide(Pattern.compile(regex));
    }

    public RuleBuilder<TYPE, ANNOTATION> keyword(TYPE type, Pattern pattern) {
        return addToConcat(Parser.keyword(type, pattern));
    }

    public RuleBuilder<TYPE, ANNOTATION> keyword(TYPE type, String regex) {
        return keyword(type, Pattern.compile(regex));
    }

    public RuleBuilder<TYPE, ANNOTATION> rule(String name) {
        return addToConcat(new PlaceholderParser<>(parserBuilder, name));
    }

    public RuleBuilder<TYPE, ANNOTATION> or() {
        addClauseAndErase();
        currentSubrule = null;
        return this;
    }

    public void end() {
        addClauseAndErase();
        editable = false;
        parserBuilder.addParser(name, rule);
    }

    private void addClauseAndErase() {
        if (currentSubrule != null) {
            rule.addSubparser(currentSubrule);
            currentSubrule = null;
        }
    }

    private RuleBuilder<TYPE, ANNOTATION> addToConcat(Parser<TYPE, ANNOTATION> parser) {
        if (!editable) return this;
        if (currentSubrule == null) genNewSubrule();
        currentSubrule.addSubparser(parser);
        return this;
    }

    private void genNewSubrule(Function<List<AST<TYPE, ANNOTATION>>, AST<TYPE, ANNOTATION>> atSuccess) {
        if (currentSubrule == null) currentSubrule = new ConcatParser<>(atSuccess);
    }

    private void genNewSubrule() {
        if (currentSubrule == null) currentSubrule = Parser.concat(null, new ArrayList<>());
    }
}
