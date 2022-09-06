package org.parser.base.build;

import org.parser.Consumable;
import org.parser.base.OrParser;
import org.parser.base.Parser;
import org.parser.base.RegExParser;
import org.parser.tree.AST;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * The rule builder allows building rules (i.e. a line in the BNF).
 * @param <TYPE> type of the AST
 * @param <ANNOTATION> annotation of the AST
 */
public class RuleBuilder<TYPE, ANNOTATION> {
    /**
     * Rule name (with this you will be able to access it)
     */
    private final String ruleName;
    /**
     * The underlying ParserBuilder. This is used for the rule method, so that a * placeholder can be added to it, and for creating the
     * placeholder can be added to it and for creating the ConcatRuleBuilder and NextIsOrBuilder.
     */
    private final ParserBuilder<TYPE, ANNOTATION> parserBuilder;
    /**
     * The final rule
     */
    private final OrParser<TYPE, ANNOTATION> rule;
    /**
     * Indicates whether the RuleBuilder is still modifiable or not.
     */
    private boolean frozen;

    /**
     * The nextIsOrBuilder (this can be used over and over again and does not have to be always recreated
     * or be reset)
     */
    private final NextIsOrBuilder<TYPE, ANNOTATION> nextIsOrBuilder;
    /**
     * The ConcatRuleBuilder (this can be used over and over again and does not have to be regenerated every time,
     * because it can be easily reset)
     */
    private final ConcatRuleBuilder<TYPE, ANNOTATION> concatRuleBuilder;

    public RuleBuilder(String ruleName, ParserBuilder<TYPE, ANNOTATION> parserBuilder) {
        this.ruleName = ruleName;
        this.parserBuilder = parserBuilder;
        this.rule = Parser.or(new ArrayList<>());
        this.nextIsOrBuilder = new NextIsOrBuilder<>(parserBuilder, this);
        this.concatRuleBuilder = new ConcatRuleBuilder<>(parserBuilder, this);
        this.frozen = false;
    }

    /**
     * Creates a new subrule consisting of multiple elements.
     * @param atSuccess Called when the ConcatParser succeeds.
     * @return A reset ConcatRuleBuilder whose ConcatParser uses the passed atSuccess method.
     */
    private ConcatRuleBuilder<TYPE, ANNOTATION> concat(Function<List<AST<TYPE, ANNOTATION>>, AST<TYPE, ANNOTATION>> atSuccess) {
        if (!frozen) concatRuleBuilder.newSubrule(atSuccess);
        return concatRuleBuilder;
    }

    /**
     * Creates a new subrule consisting of multiple elements.
     * (Takes as AST the first child, if any).
     * @return A reset ConcatRuleBuilder whose ConcatParser simply takes as AST the AST of the first child.
     */
    public ConcatRuleBuilder<TYPE, ANNOTATION> concat() {
        return concat(trees -> trees.size() >= 1 ? trees.get(0) : new AST<TYPE, ANNOTATION>(null).setIgnore(true));
    }

    /**
     * Creates a new subrule consisting of several elements.
     * @param type Type of the AST.
     * @return A reset ConcatRuleBuilder whose ConcatParser has as its AST the type passed and
     * simply has as children the ASTs of the elements of the subrule.
     */
    public ConcatRuleBuilder<TYPE, ANNOTATION> concat(TYPE type) {
        return concat(Parser.basicConcatAtSuccess(type));
    }

    /**
     * Creates a match parser as a new clause (for multi-element clauses, concat must be called first).
     * @param type Type of the AST
     * @param pattern Pattern
     * @return Returns the NextIsOrBuilder.
     */
    public NextIsOrBuilder<TYPE, ANNOTATION> match(TYPE type, Pattern pattern) {
        return addSingleClause(Parser.match(type, pattern));
    }

    /**
     * Creates a match parser as a new clause (for multi-element clauses, concat must be called first).
     * @param type Type of the AST
     * @param regex RegEx
     * @return Returns the NextIsOrBuilder.
     */
    public NextIsOrBuilder<TYPE, ANNOTATION> match(TYPE type, String regex) {
        return match(type, parserBuilder.getPattern(regex));
    }

    /**
     * Creates a keyword parser as a new clause (for multi-element clauses, concat must be called first).
     * @param type Type of the AST
     * @param pattern Pattern
     * @return Returns the NextIsOrBuilder.
     */
    public NextIsOrBuilder<TYPE, ANNOTATION> keyword(TYPE type, Pattern pattern) {
        return addSingleClause(Parser.keyword(type, pattern));
    }

    /**
     * Creates a keyword parser as a new clause (for multi-element clauses, concat must be called first).
     * @param type Type of the AST
     * @param regex RegEx
     * @return Returns the NextIsOrBuilder.
     */
    public NextIsOrBuilder<TYPE, ANNOTATION> keyword(TYPE type, String regex) {
        return keyword(type, parserBuilder.getPattern(regex));
    }

    /**
     * Creates a customRegEx parser as a new clause (for multi-element clauses, concat must be called first).
     * @param atSuccess Will be called when the RegEx parser succeeds.
     * @param pattern Pattern
     * @return Returns the NextIsOrBuilder.
     */
    public NextIsOrBuilder<TYPE, ANNOTATION> customRegEx(Function<Consumable.Match, AST<TYPE, ANNOTATION>> atSuccess,
                                                         Pattern pattern) {
        return addSingleClause(new RegExParser<>(pattern, atSuccess));
    }

    /**
     * Creates a customRegEx parser as a new clause (for multi-element clauses, concat must be called first).
     * @param atSuccess Called when the RegEx parser succeeds.
     * @param regex RegEx
     * @return Returns the NextIsOrBuilder.
     */
    public NextIsOrBuilder<TYPE, ANNOTATION> customRegEx(Function<Consumable.Match, AST<TYPE, ANNOTATION>> atSuccess,
                                                         String regex) {
        return customRegEx(atSuccess, parserBuilder.getPattern(regex));
    }

    /**
     * Creates as a new clause a placeholder parser that will map the rule with the passed name.
     * @param name rule name
     * @return Returns the NextIsOrBuilder.
     */
    public NextIsOrBuilder<TYPE, ANNOTATION> rule(String name) {
        return addSingleClause(parserBuilder.getPlaceholder(name));
    }

    /**
     * Creates as a new clause an optional-parser that will map the rule with the name passed in.
     * @param name rule name
     * @return Returns the NextIsOrBuilder.
     */
    public NextIsOrBuilder<TYPE, ANNOTATION> optional(String name) {
        return addSingleClause(Parser.optional(parserBuilder.getPlaceholder(name)));
    }

    /**
     * Creates as a new clause a many-parser that will map the rule with the name passed in.
     * @param type Type to which the many expression will be mapped.
     * @param name rule name
     * @return Returns the NextIsOrBuilder.
     */
    public NextIsOrBuilder<TYPE, ANNOTATION> many(TYPE type, String name) {
        return addSingleClause(parserBuilder.getMany(type, name));
    }

    /**
     * Creates a Some-Rule as a new clause, i.e. a concat of first the Rule with the passed
     * name and then a Many-Rule with the passed name.
     * @param type Type of the resulting AST.
     * @param name rule name
     * @return Returns the NextIsOrBuilder.
     */
    public NextIsOrBuilder<TYPE, ANNOTATION> some(TYPE type, String name) {
        var placeholder = parserBuilder.getPlaceholder(name);
        var many = parserBuilder.getMany(null, name);
        return addSingleClause(Parser.concat(type, List.of(placeholder, many)));
    }

    /**
     *
     * @return Returns the rule name
     */
    public String getName() {
        return ruleName;
    }

    /**
     * Inserts a single-element parser as a new clause.
     * @param singleParser one-element parser
     * @return Returns the NextIsOrBuilder.
     */
    private NextIsOrBuilder<TYPE, ANNOTATION> addSingleClause(Parser<TYPE, ANNOTATION> singleParser) {
        addClause(singleParser);
        return nextIsOrBuilder;
    }

    /**
     * Inserts the passed parser as a new clause.
     * @param parser Parser
     */
    void addClause(Parser<TYPE, ANNOTATION> parser) {
        if (!frozen) rule.addSubparser(parser);
    }

    /**
     * Freezes the RuleBuilder so that method calls no longer have any effect and returns the resulting
     * parser of the RuleBuilder.
     * @return The parser that emerges from the RuleBuilder.
     */
    Parser<TYPE, ANNOTATION> freeze() {
        frozen = true;
        return rule;
    }
}
