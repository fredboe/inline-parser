package org.parser.base.build;

import org.parser.GenPattern;
import org.parser.base.*;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Dient zur Erzeugung eines ParserPools
 * @param <TYPE> Typ für den AST
 * @param <ANNOTATION> Annotation für den AST
 */
public class ParserBuilder<TYPE, ANNOTATION> {
    private Map<String, Parser<TYPE, ANNOTATION>> namedParsers;
    private Map<String, PlaceholderParser<TYPE, ANNOTATION>> placeholders;

    private final GenPattern utils;

    public ParserBuilder() {
        this.namedParsers = new HashMap<>();
        this.placeholders = new HashMap<>();
        this.utils = new GenPattern();
    }

    public ParserBuilder(GenPattern.Flag flag) {
        this.namedParsers = new HashMap<>();
        this.placeholders = new HashMap<>();
        this.utils = new GenPattern(flag);
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
        if (name != null && parser != null) namedParsers.put(name, parser);
    }

    PlaceholderParser<TYPE, ANNOTATION> getPlaceholder(String name) {
        if (name == null) return null;
        if (placeholders.containsKey(name)) return placeholders.get(name);

        PlaceholderParser<TYPE, ANNOTATION> placeholder = new PlaceholderParser<>();
        placeholders.put(name, placeholder);
        return placeholder;
    }

    public NewRuleInvocationChain newRule(String name) {
        return new NewRuleInvocationChain(new RuleBuilder<>(name, this));
    }

    Pattern getPattern(String regex) {
        return utils.getPattern(regex);
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
