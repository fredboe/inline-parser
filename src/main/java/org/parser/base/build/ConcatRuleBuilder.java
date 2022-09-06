package org.parser.base.build;

import org.parser.base.ConcatParser;
import org.parser.base.Parser;
import org.parser.tree.AST;

import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * ConcatRuleBuilder allows building a concat subrule (i.e. multiple regex or rule parsers in a row).
 * @param <TYPE> type of the AST
 * @param <ANNOTATION> annotation of the AST
 */
public class ConcatRuleBuilder<TYPE, ANNOTATION> {
    /**
     * ParserBuilder, in the ConcatRuleBuilder this is used for the rule parser to add a
     * placeholder parser to the placeholder map so that it can be built later and for
     * the end method, so that the underlying rule can be added to the ParserBuilder.
     */
    private final ParserBuilder<TYPE, ANNOTATION> parserBuilder;
    /**
     * RuleBuilder, in ConcatRuleBuilder this is used to add the subrule as a new clause when using or() or end().
     */
    private final RuleBuilder<TYPE, ANNOTATION> ruleBuilder;

    /**
     * The manyBuilder (this can be used over and over again and does not have to be always recreated
     * or be reset)
     */
    private final ManyBuilder<TYPE, ANNOTATION> manyBuilder;
    /**
     * The someBuilder (this can be used over and over again and does not need to be recreated every time,
     * because it can be easily reset)
     */
    private final SomeBuilder<TYPE, ANNOTATION> someBuilder;

    /**
     * Subrule (ConcatParser of the individual RegEx/Placholder parsers. This will be added later as a clause to the RuleBuilder).
     */
    private ConcatParser<TYPE, ANNOTATION> subrule;
    /**
     * Indicates whether the ConcatRuleBuilder allows any other method calls.
     */
    private boolean frozen;

    public ConcatRuleBuilder(ParserBuilder<TYPE, ANNOTATION> parserBuilder, RuleBuilder<TYPE, ANNOTATION> ruleBuilder) {
        if (parserBuilder == null || ruleBuilder == null)
            throw new RuntimeException("Parser-Builder und Rule-Builder dürfen nicht null sein");
        this.parserBuilder = parserBuilder;
        this.ruleBuilder = ruleBuilder;
        this.subrule = null;
        this.manyBuilder = new ManyBuilder<>(parserBuilder, this);
        this.someBuilder = new SomeBuilder<>(parserBuilder, this);
        this.frozen = true;
    }

    /**
     * Creates a new subrule (ConcatParser) with the passed atSuccess method.
     * @param atSuccess Called on ConcatParser success.
     */
    void newSubrule(Function<List<AST<TYPE, ANNOTATION>>, AST<TYPE, ANNOTATION>> atSuccess) {
        if (frozen) {
            this.subrule = new ConcatParser<>(atSuccess);
            frozen = false;
        }
    }

    /**
     * Creates a new subrule (ConcatParser) using the Parser.basicConcatAtSuccess method.
     * @param type Type to which the concatenation will be combined.
     */
    void newSubrule(TYPE type) {
        newSubrule(Parser.basicConcatAtSuccess(type));
    }

    /**
     * Inserts a many parser as a new step to the current subrule.
     * @param pattern Pattern
     * @return The underlying ConcatRuleBuilder.
     */
    public ConcatRuleBuilder<TYPE, ANNOTATION> match(TYPE type, Pattern pattern) {
        return addStep(Parser.match(type, pattern));
    }

    /**
     * Inserts a many parser as a new step to the current subrule.
     * @param regex RegEx
     * @return The underlying ConcatRuleBuilder.
     */
    public ConcatRuleBuilder<TYPE, ANNOTATION> match(TYPE type, String regex) {
        return match(type, parserBuilder.getPattern(regex));
    }


    /**
     * Inserts a keyword parser as a new step to the current subrule.
     * @param pattern Pattern
     * @return The underlying ConcatRuleBuilder.
     */
    public ConcatRuleBuilder<TYPE, ANNOTATION> keyword(TYPE type, Pattern pattern) {
        return addStep(Parser.keyword(type, pattern));
    }

    /**
     * Inserts a keyword parser as a new step to the current subrule.
     * @param regex RegEx
     * @return The underlying ConcatRuleBuilder.
     */
    public ConcatRuleBuilder<TYPE, ANNOTATION> keyword(TYPE type, String regex) {
        return keyword(type, parserBuilder.getPattern(regex));
    }

    /**
     * Inserts a hide parser as a new step to the current subrule.
     * @param pattern Pattern
     * @return The underlying ConcatRuleBuilder.
     */
    public ConcatRuleBuilder<TYPE, ANNOTATION> hide(Pattern pattern) {
        return addStep(Parser.hide(pattern));
    }

    /**
     * Inserts a hide parser as a new step to the current subrule.
     * @param regex RegEx
     * @return The underlying ConcatRuleBuilder.
     */
    public ConcatRuleBuilder<TYPE, ANNOTATION> hide(String regex) {
        return hide(parserBuilder.getPattern(regex));
    }

    /**
     * Inserts a placeholder parser with the supplied name as a new step to the current subrule.
     * @param name name of the rule
     * @return The underlying ConcatRuleBuilder.
     */
    public ConcatRuleBuilder<TYPE, ANNOTATION> rule(String name) {
        return addStep(parserBuilder.getPlaceholder(name));
    }

    /**
     * Inserts a placeholder parser with the supplied name as a new step to the current subrule.
     * @param name name of the rule
     * @return The underlying ConcatRuleBuilder.
     */
    public ConcatRuleBuilder<TYPE, ANNOTATION> optional(String name) {
        return addStep(Parser.optional(parserBuilder.getPlaceholder(name)));
    }


    /**
     * Inserts a many parser with the supplied name as a new step to the current subrule.
     * @param name name of the rule
     * @return The underlying ConcatRuleBuilder.
     */
    public ConcatRuleBuilder<TYPE, ANNOTATION> many(String name) {
        return addStep(parserBuilder.getMany(null, name));
    }

    /**
     * Creates a new ManyBuilder, which is then attached to the subrule.
     * @return A new ManyBuilder
     */
    public ManyBuilder<TYPE, ANNOTATION> many() {
        manyBuilder.newManyRule();
        return manyBuilder;
    }

    /**
     * Inserts into the current subrule as a new step first the rule with the passed name, and then
     * inserts a many-parser that will map the rule with the passed name.
     * @param name Name of the rule
     * @return The underlying ConcatRuleBuilder.
     */
    public ConcatRuleBuilder<TYPE, ANNOTATION> some(String name) {
        this.rule(name);
        return this.many(name);
    }

    /**
     * Creates a new SomeBuilder, which is then attached to the subrule.
     * @return A new SomeBuilder
     */
    public SomeBuilder<TYPE, ANNOTATION> some() {
        someBuilder.newSomeRule();
        return someBuilder;
    }

    /**
     * Inserts the current subrule as a clause into the underlying rule and returns the RuleBuilder.
     * The or method pauses this ConcatRuleBuilder so that method calls other than newSubrule have no * effect on this builder.
     * have any effect on this builder.
     * @return The underlying RuleBuilder.
     */
    public RuleBuilder<TYPE, ANNOTATION> or() {
        frozen = true;
        ruleBuilder.addClause(subrule);
        return ruleBuilder;
    }

    /**
     * Terminates this ConcatRuleBuilder and also the underlying RuleBuilder. The resulting rule
     * is then added to the ParserBuilder.
     */
    public void end() {
        if (!subrule.isEmpty()) {
            ruleBuilder.addClause(subrule);
            subrule = null;
        }
        frozen = true;
        parserBuilder.addParser(ruleBuilder.getName(), ruleBuilder.freeze());
    }

    /**
     * Inserts a new step to the current subrule if this ConcatRuleBuilder is not paused.
     * @param parser Parser
     * @return The underlying ConcatRuleBuilder.
     */
    public ConcatRuleBuilder<TYPE, ANNOTATION> addStep(Parser<TYPE, ANNOTATION> parser) {
        if (!frozen && subrule != null) subrule.addSubparser(parser);
        return this;
    }
}
