package org.parser.base.build;

import org.parser.base.*;

import java.util.*;

/**
 * Dient zur Erzeugung eines ParserPools
 * @param <TYPE> Typ für den AST
 * @param <ANNOTATION> Annotation für den AST
 */
public class ParserBuilder<TYPE, ANNOTATION> {
    private Map<String, Parser<TYPE, ANNOTATION>> namedParsers;
    private Map<String, PlaceholderParser<TYPE, ANNOTATION>> placeholders;

    public ParserBuilder() {
        this.namedParsers = new HashMap<>();
        this.placeholders = new HashMap<>();
    }

    public ParserPool<TYPE, ANNOTATION> build() {
        buildPlaceholders();
        ParserPool<TYPE, ANNOTATION> pool = new ParserPool<>(namedParsers);
        clear();
        return pool;
    }

    private void buildPlaceholders() {
        placeholders.forEach((name, placeholder) -> {
            var parser = namedParsers.get(name);
            placeholder.setParserIfNull(parser);
        });
    }

    public void clear() {
        namedParsers = null;
        placeholders = null;
    }

    void addParser(String name, Parser<TYPE, ANNOTATION> parser) {
        namedParsers.put(name, parser);
    }

    PlaceholderParser<TYPE, ANNOTATION> getPlaceholder(String name) {
        if (placeholders.containsKey(name)) return placeholders.get(name);
        PlaceholderParser<TYPE, ANNOTATION> placeholder = new PlaceholderParser<>();
        placeholders.put(name, placeholder);
        return placeholder;
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
