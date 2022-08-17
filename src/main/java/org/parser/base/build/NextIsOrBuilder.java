package org.parser.base.build;

public class NextIsOrBuilder<TYPE, ANNOTATION> {
    private final ParserBuilder<TYPE, ANNOTATION> parserBuilder;
    private final RuleBuilder<TYPE, ANNOTATION> ruleBuilder;

    public NextIsOrBuilder(ParserBuilder<TYPE, ANNOTATION> parserBuilder, RuleBuilder<TYPE, ANNOTATION> ruleBuilder) {
        this.parserBuilder = parserBuilder;
        this.ruleBuilder = ruleBuilder;
    }

    public RuleBuilder<TYPE, ANNOTATION> or() {
        return ruleBuilder;
    }

    public void end() {
        parserBuilder.addParser(ruleBuilder.getName(), ruleBuilder.freeze());
    }
}
