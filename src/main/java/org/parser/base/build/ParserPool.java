package org.parser.base.build;

import org.parser.base.*;

import java.util.*;

/**
 * Dient zur Erzeugung eines ParserPools
 * @param <TYPE> Typ für den AST
 * @param <ANNOTATION> Annotation für den AST
 */
public class ParserPool<TYPE, ANNOTATION> {
    private final Map<String, Parser<TYPE, ANNOTATION>> namedParsers;

    public ParserPool() {
        this.namedParsers = new HashMap<>();
    }

    public Optional<Parser<TYPE, ANNOTATION>> getParser(String name) {
        return Optional.ofNullable(namedParsers.get(name));
    }

    public void addParser(String name, Parser<TYPE, ANNOTATION> parser) {
        namedParsers.put(name, parser);
    }

    public NewRuleInvocationChain newRule(String name, TYPE type) {
        return new NewRuleInvocationChain(new RuleBuilder<>(this, name, type));
    }


    public class NewRuleInvocationChain {
        private final RuleBuilder<TYPE, ANNOTATION> ruleBuilder;

        public NewRuleInvocationChain(RuleBuilder<TYPE, ANNOTATION> ruleBuilder) {
            this.ruleBuilder = ruleBuilder;
        }

        public RuleBuilder<TYPE, ANNOTATION> consistsOf() {
            return ruleBuilder;
        }
    }
}
