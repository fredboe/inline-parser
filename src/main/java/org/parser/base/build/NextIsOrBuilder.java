package org.parser.base.build;

/**
 * The NextIsOrBuilder is only used to force the user to call the or method next,
 * or the end method, which terminates the whole rule.
 * @param <TYPE> type of AST
 * @param <ANNOTATION> annotation of the AST
 */
public class NextIsOrBuilder<TYPE, ANNOTATION> {
    /**
     * The underlying ParserBuilder. This is needed in the end method so that the resulting
     * Rule can be added to it.
     */
    private final ParserBuilder<TYPE, ANNOTATION> parserBuilder;
    /**
     * The underlying RuleBuilder. This is needed so that when the or method is called the rule
     * can be added.
     */
    private final RuleBuilder<TYPE, ANNOTATION> ruleBuilder;

    public NextIsOrBuilder(ParserBuilder<TYPE, ANNOTATION> parserBuilder, RuleBuilder<TYPE, ANNOTATION> ruleBuilder) {
        this.parserBuilder = parserBuilder;
        this.ruleBuilder = ruleBuilder;
    }

    /**
     *
     * @return Simply returns the underlying RuleBuilder.
     */
    public RuleBuilder<TYPE, ANNOTATION> or() {
        return ruleBuilder;
    }

    /**
     * Terminates the RuleBuilder so that a method call on it does nothing more.
     * The resulting rule is then added to the ParserBuilder.
     */
    public void end() {
        parserBuilder.addParser(ruleBuilder.getName(), ruleBuilder.freeze());
    }
}
