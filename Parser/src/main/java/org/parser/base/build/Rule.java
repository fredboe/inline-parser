package org.parser.base.build;

import org.parser.base.ConcatParser;
import org.parser.base.OrParser;
import org.parser.base.Parser;
import org.parser.tree.AST;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * This class represents a rule (a rule is basically one line in the backus-naur-form).
 * @param <TYPE> Type of the ASTs
 */
public class Rule<TYPE> {
    /**
     * Name of the rule
     */
    private final String name;
    /**
     * The underlying ParserBuilder the rule should be added to.
     */
    private final ParserBuilder<TYPE> parserBuilder;
    /**
     * The resulting rule
     */
    private final OrParser<TYPE> rule;
    /**
     * The current subrule (concat-parser) that will be added as a subparser to the rule
     */
    private ConcatParser<TYPE> currentSubrule;
    /**
     * Determines whether the rule method-invocations change this rule.
     */
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

    /**
     * Changes the atSuccess-Method of the current subrule to the given method.
     * @param atSuccess AtSuccess-Method
     * @return Returns the underlying rule.
     */
    public Rule<TYPE> type(Function<List<AST<TYPE>>, AST<TYPE>> atSuccess) {
        newSubruleIfNotExists();
        currentSubrule.setAtSuccess(atSuccess);
        return this;
    }

    /**
     * Changes the atSuccess-Method of the current subrule to childrenIfNoType with the given type.
     * So that if one parser of the subrule returns an AST with the type null then the children of
     * the AST are taken over as the children of the resulting AST.
     * @param type Type of the resulting AST
     * @return Returns the underlying rule.
     */
    public Rule<TYPE> type(TYPE type) {
        return type(Mode.takeChildrenIfTypeNull(type));
    }

    /**
     * Adds a new match-parser to the current subrule.
     * @param type Type
     * @param pattern Pattern to look for
     * @return Returns the underlying rule.
     */
    public Rule<TYPE> match(TYPE type, Pattern pattern) {
        return addToCurrentSubrule(Parser.match(type, pattern));
    }

    /**
     * Adds a new match-parser to the current subrule.
     * @param type Type
     * @param regex RegEx to look for
     * @return Returns the underlying rule.
     */
    public Rule<TYPE> match(TYPE type, String regex) {
        return match(type, Pattern.compile(regex));
    }

    /**
     * Adds a new hide-parser to the current subrule.
     * @param pattern Pattern to look for
     * @return Returns the underlying rule.
     */
    public Rule<TYPE> hide(Pattern pattern) {
        return addToCurrentSubrule(Parser.hide(pattern));
    }

    /**
     * Adds a new hide-parser to the current subrule.
     * @param regex RegEx to look for
     * @return Returns the underlying rule.
     */
    public Rule<TYPE> hide(String regex) {
        return hide(Pattern.compile(regex));
    }

    /**
     * Adds a new keyword-parser to the current subrule.
     * @param type Type
     * @param pattern Pattern to look for
     * @return Returns the underlying rule.
     */
    public Rule<TYPE> keyword(TYPE type, Pattern pattern) {
        return addToCurrentSubrule(Parser.keyword(type, pattern));
    }

    /**
     * Adds a new keyword-parser to the current subrule.
     * @param type Type
     * @param regex RegEx to look for
     * @return Returns the underlying rule.
     */
    public Rule<TYPE> keyword(TYPE type, String regex) {
        return keyword(type, Pattern.compile(regex));
    }

    /**
     * Adds the rule with the given name to the current subrule.
     * @param name rule name
     * @return Returns the underlying rule.
     */
    public Rule<TYPE> rule(String name) {
        return addToCurrentSubrule(parserBuilder.getPlaceholder(name));
    }

    /**
     * Adds a many-parser of the rule with the given name to the current subrule.
     * @param type Type of the resulting AST
     * @param ruleName rule name
     * @return Returns the underlying rule.
     */
    public Rule<TYPE> many(TYPE type, String ruleName) {
        return addToCurrentSubrule(Parser.many(type, parserBuilder.getPlaceholder(ruleName)));
    }

    /**
     * Adds a many-parser of the rule with the given name to the current subrule.
     * If the type of the current subrule is childrenIfNoType then the list of ASTs from the
     * many-parser are added to the AST of the current subrule as many children, otherwise the list of ASTs is
     * added as one child with type null.
     * @param ruleName rule name
     * @return Returns the underlying rule.
     */
    public Rule<TYPE> many(String ruleName) {
        return many(null, ruleName);
    }

    /**
     * Adds a many-parser of the given simplerule (just a concat of match, hide, keyword and rule) to the
     * current subrule.
     * @param type Type of the resulting AST
     * @param simplerule simplerule
     * @return Returns the underlying rule.
     */
    public Rule<TYPE> many(TYPE type, Simplerule<TYPE> simplerule) {
        return addToCurrentSubruleWithSubsubrule(rule -> Parser.many(type, rule), simplerule);
    }

    /**
     * Adds a many-parser of the given simplerule (just a concat of match, hide, keyword and rule) to the
     * current subrule.
     * If the type of the current subrule is childrenIfNoType then the list of ASTs from the
     * many-parser are added to the AST of the current subrule as many children, otherwise the list of ASTs is
     * added as one child with type null.
     * @param simplerule simplerule
     * @return Returns the underlying rule.
     */
    public Rule<TYPE> many(Simplerule<TYPE> simplerule) {
        return many(null, simplerule);
    }

    /**
     * Adds a some-parser of the rule with the given name to the current subrule.
     * @param type Type of the resulting AST
     * @param ruleName rule name
     * @return Returns the underlying rule.
     */
    public Rule<TYPE> some(TYPE type, String ruleName) {
        return addToCurrentSubrule(Parser.some(type, parserBuilder.getPlaceholder(ruleName)));
    }

    /**
     * Adds a some-parser of the rule with the given name to the current subrule.
     * If the type of the current subrule is childrenIfNoType then the list of ASTs from the
     * some-parser are added to the AST of the current subrule as many children, otherwise the list of ASTs is
     * added as one child with type null.
     * @param ruleName rule name
     * @return Returns the underlying rule.
     */
    public Rule<TYPE> some(String ruleName) {
        return some(null, ruleName);
    }

    /**
     * Adds a some-parser of the given simplerule (just a concat of match, hide, keyword and rule) to the
     * current subrule.
     * @param type Type of the resulting AST
     * @param simplerule simplerule
     * @return Returns the underlying rule.
     */
    public Rule<TYPE> some(TYPE type, Simplerule<TYPE> simplerule) {
        return addToCurrentSubruleWithSubsubrule(rule -> Parser.some(type, rule), simplerule);
    }

    /**
     * Adds a some-parser of the given simplerule (just a concat of match, hide, keyword and rule) to the
     * current subrule.
     * If the type of the current subrule is childrenIfNoType then the list of ASTs from the
     * some-parser are added to the AST of the current subrule as many children, otherwise the list of ASTs is
     * added as one child with type null.
     * @param simplerule simplerule
     * @return Returns the underlying rule.
     */
    public Rule<TYPE> some(Simplerule<TYPE> simplerule) {
        return some(null, simplerule);
    }

    /**
     * Adds an optional-parser of the rule with the given name to the current subrule.
     * @param ruleName rule name
     * @return Returns the underlying rule.
     */
    public Rule<TYPE> optional(String ruleName) {
        return addToCurrentSubrule(Parser.optional(parserBuilder.getPlaceholder(ruleName)));
    }

    /**
     * Adds an optional-parser of the given simplerule (just a concat of match, hide, keyword and rule) to the
     * current subrule.
     * @param simplerule simplerule
     * @return Returns the underlying rule.
     */
    public Rule<TYPE> optional(Simplerule<TYPE> simplerule) {
        return addToCurrentSubruleWithSubsubrule(Parser::optional, simplerule);
    }

    /**
     * Adds a subsubrule to the current subrule.
     * This is there so that you can give a section from the subrule its own type.
     * @param simplerule simplerule
     * @return Returns the underlying rule.
     */
    public Rule<TYPE> subsubrule(Simplerule<TYPE> simplerule) {
        return addToCurrentSubruleWithSubsubrule(rule -> rule, simplerule);
    }

    /**
     * Adds a parser that contains a subsubrule to the current subrule.
     * @param subruleMapper maps the parser of the given subsubrule (concat-parser) to the parser that
     *                      should be added to the current subrule.
     * @param subsubrule subsubrule
     * @return Returns the underlying rule.
     */
    private Rule<TYPE> addToCurrentSubruleWithSubsubrule(Function<ConcatParser<TYPE>, Parser<TYPE>> subruleMapper,
                                                         Simplerule<TYPE> subsubrule) {
        subsubrule.freeze();
        if (!frozen) parserBuilder.unite(subsubrule.parserBuilder());
        return addToCurrentSubrule(subruleMapper.apply(subsubrule.parser()));
    }

    /**
     * Adds the current subrule (if it's not empty) as a new clause to the rule. Then the current subrule
     * will be replaced with a new subrule.
     * @return Returns the underlying rule.
     */
    public Rule<TYPE> or() {
        storeCurrentSubruleAndReset();
        return this;
    }

    /**
     * Freezes this rule and adds it to the parser-builder.
     */
    public void end() {
        if (!frozen) {
            freeze();
            parserBuilder.addParser(name, rule);
        }
    }

    /**
     * Freezes this rule so that a new method invocation does not change this rule.
     * freeze also adds the current subrule to the rule if it's not empty.
     */
    public void freeze() {
        storeCurrentSubruleAndReset();
        frozen = true;
    }

    private void storeCurrentSubruleAndReset() {
        if (!frozen) {
            if (currentSubrule != null && !currentSubrule.isEmpty()) rule.addSubparser(currentSubrule);
            currentSubrule = null;
        }
    }

    /**
     * Adds a parser to the current subrule.
     * @param parser Parser
     * @return Returns the underlying rule.
     */
    private Rule<TYPE> addToCurrentSubrule(Parser<TYPE> parser) {
        if (!frozen) {
            newSubruleIfNotExists();
            currentSubrule.addSubparser(parser);
        }
        return this;
    }

    /**
     * Creates a new subrule if it's null. The default type is justFst.
     */
    private void newSubruleIfNotExists() {
        if (currentSubrule == null) {
            currentSubrule = new ConcatParser<>(Mode.takeFirstChild());
        }
    }
}

