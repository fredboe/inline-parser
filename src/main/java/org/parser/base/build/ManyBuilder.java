package org.parser.base.build;

import org.parser.base.ConcatParser;
import org.parser.base.ManyParser;
import org.parser.base.Parser;

import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * The ManyBuilder allows you to build a many-subrule (i.e. a parser that runs,
 * until it fails).
 * @param <TYPE> type of AST
 * @param <ANNOTATION> annotation of the AST
 */
public class ManyBuilder<TYPE, ANNOTATION> {
    /**
     * The ParserBuilder is used to get new placeholderParser.
     */
    private final ParserBuilder<TYPE, ANNOTATION> parserBuilder;
    /**
     * The ConcatRuleBuilder is used to, when manyEnd() is called, add the resulting many-expression
     * in the Concat-Rule
     */
    private final ConcatRuleBuilder<TYPE, ANNOTATION> concatBuilder;
    /**
      The parser that is wrapped with many .
     */
    private ConcatParser<TYPE, ANNOTATION> concatParser;
    /**
     * Indicates whether the ManyBuilder allows any other method calls.
     */
    private boolean frozen;

    public ManyBuilder(ParserBuilder<TYPE, ANNOTATION> parserBuilder,
                       ConcatRuleBuilder<TYPE, ANNOTATION> concatBuilder) {
        this.parserBuilder = parserBuilder;
        this.concatBuilder = concatBuilder;
        this.concatParser = Parser.concat(null, new ArrayList<>());
        this.frozen = true;
    }

    /**
     * Creates a new many-rule if the old one was already frozen.
     */
    void newManyRule() {
        if (frozen) {
            concatParser = Parser.concat(null, new ArrayList<>());
            frozen = false;
        }
    }

    /**
     * Inserts a hide parser as a new step to the current many-rule.
     * @param pattern Pattern
     * @return The underlying ManyBuilder.
     */
    public ManyBuilder<TYPE, ANNOTATION> match(Pattern pattern) {
        return addStep(Parser.hide(pattern));
    }

    /**
     * Adds a hide parser to the current many-rule as a new step.
     * @param regex RegEx
     * @return The underlying ManyBuilder.
     */
    public ManyBuilder<TYPE, ANNOTATION> match(String regex) {
        return match(parserBuilder.getPattern(regex));
    }

    /**
     * Adds as a new step to the current many-rule a placeholder parser that will map the rule with the passed
     * name passed in.
     * @param name Name of the rule
     * @return The underlying ManyBuilder.
     */
    public ManyBuilder<TYPE, ANNOTATION> rule(String name) {
        return addStep(parserBuilder.getPlaceholder(name));
    }

    /**
     * Freezes this manyBuilder and stores the many expression in the ConcatRuleBuilder.
     * @return Returns the ConcatRuleBuilder where the many expression was stored.
     */
    public ConcatRuleBuilder<TYPE, ANNOTATION> manyEnd() {
        frozen = true;
        if (!concatParser.isEmpty()) concatBuilder.addStep(new ManyParser<>(null, concatParser));
        return concatBuilder;
    }

    /**
     * Adds a new step to the current many expression if this ManyBuilder is not frozen.
     * @param parser Parser
     * @return The underlying ManyBuilder.
     */
    private ManyBuilder<TYPE, ANNOTATION> addStep(Parser<TYPE, ANNOTATION> parser) {
        if (parser != null && !frozen) concatParser.addSubparser(parser);
        return this;
    }
}