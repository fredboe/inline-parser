package org.parser.base.build;

import org.parser.base.ConcatParser;
import org.parser.base.ManyParser;
import org.parser.base.Parser;

import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * The SomeBuilder allows to build a some-subrule (i.e. a parser that is executed,
 * until it fails, where this parser must succeed at least once).
 * @param <TYPE> type of AST.
 * @param <ANNOTATION> annotation of the AST.
 */
public class SomeBuilder<TYPE, ANNOTATION> {
    /**
     * The ParserBuilder is used to get new placeholder parsers.
     */
    private final ParserBuilder<TYPE, ANNOTATION> parserBuilder;
    /**
     * The ConcatRuleBuilder is used to store the resulting some expression when calling someEnd().
     * in the Concat-Rule.
     */
    private final ConcatRuleBuilder<TYPE, ANNOTATION> concatBuilder;
    /**
     * The parser wrapped with some.
     */
    private ConcatParser<TYPE, ANNOTATION> concatParser;
    /**
     * Indicates whether the SomeBuilder allows any other method calls.
     */
    private boolean frozen;

    public SomeBuilder(ParserBuilder<TYPE, ANNOTATION> parserBuilder, ConcatRuleBuilder<TYPE, ANNOTATION> concatBuilder) {
        this.parserBuilder = parserBuilder;
        this.concatBuilder = concatBuilder;
        this.concatParser = Parser.concat(null, new ArrayList<>());
        this.frozen = true;
    }

    /**
     * Creates a new some-rule if the old one was already frozen.
     */
    void newSomeRule() {
        if (frozen) {
            concatParser = Parser.concat(null, new ArrayList<>());
            frozen = false;
        }
    }

    /**
     * Inserts a hide parser as a new step to the current some-rule.
     * @param pattern Pattern
     * @return The underlying SomeBuilder.
     */
    public SomeBuilder<TYPE, ANNOTATION> match(Pattern pattern) {
        return addStep(Parser.hide(pattern));
    }

    /**
     * Inserts a hide parser as a new step to the current some-rule.
     * @param regex RegEx
     * @return The underlying SomeBuilder.
     */
    public SomeBuilder<TYPE, ANNOTATION> match(String regex) {
        return match(parserBuilder.getPattern(regex));
    }

    /**
     * Adds a placeholder parser to the current some-rule as a new step, which will map the rule with the given
     * name will map.
     * @param name Name of the rule
     * @return The underlying SomeBuilder.
     */
    public SomeBuilder<TYPE, ANNOTATION> rule(String name) {
        return addStep(parserBuilder.getPlaceholder(name));
    }

    /**
     * Freezes this SomeBuilder and stores the Some expression in the ConcatRuleBuilder.
     * @return Returns the ConcatRuleBuilder where the some expression was stored.
     */
    public ConcatRuleBuilder<TYPE, ANNOTATION> someEnd() {
        frozen = true;
        if (!concatParser.isEmpty()) {
            concatBuilder.addStep(concatParser);
            concatBuilder.addStep(new ManyParser<>(null, concatParser));
        }
        return concatBuilder;
    }

    /**
     * Inserts a new step into the current Some expression if this SomeBuilder is not frozen.
     * @param parser Parser
     * @return The underlying SomeBuilder.
     */
    private SomeBuilder<TYPE, ANNOTATION> addStep(Parser<TYPE, ANNOTATION> parser) {
        if (parser != null && !frozen) concatParser.addSubparser(parser);
        return this;
    }
}
