package org.parser.base.build;

import org.parser.Tuple;
import org.parser.base.*;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Used to create a ParserPool.
 * @param <TYPE> type for the AST
 * @param <ANNOTATION> annotation for the AST.
 */
public class ParserBuilder<TYPE, ANNOTATION> {
    /**
     * Stores all rules by name (A rule is one line in the BNF).
     */
    private Map<String, Parser<TYPE, ANNOTATION>> rules;
    /**
     * Stores all placeholders with the name of the rule they should represent.
     * This map must be passed through when building, as the placeholders before it do not represent a parser.
     */
    private Map<String, PlaceholderParser<TYPE, ANNOTATION>> placeholders;
    /**
     * Stores all many calls with the name of which rule they should represent.
     * This map must be passed through when building, as the placeholders before it do not represent a parser.
     */
    private Map<Tuple<String, TYPE>, ManyParser<TYPE, ANNOTATION>> manys;


    public ParserBuilder() {
        this.rules = new HashMap<>();
        this.placeholders = new HashMap<>();
        this.manys = new HashMap<>();
    }

    /**
     * Builds the ParserPool, which then contains all rules as parsers.
     * @return Returns the ParserPool which contains all rules as parser.
     */
    public ParserPool<TYPE, ANNOTATION> build() {
        buildPlaceholders();
        buildManys();
        ParserPool<TYPE, ANNOTATION> pool = new ParserPool<>(rules);
        clear();
        return pool;
    }

    /**
     * Builds the whole placeholders by replacing the parsers in the placeholders with the rules associated with the name
     * are replaced by the rules associated with the name.
     */
    private void buildPlaceholders() {
        placeholders.forEach((name, placeholder) -> {
            var parser = rules.get(name);
            placeholder.setParserIfNull(parser);
        });
    }

    /**
     * Builds the many-parsers by replacing the parsers in the many-parsers with the rules associated with the name
     * are replaced by the rules associated with the name.
     */
    private void buildManys() {
        manys.forEach((tuple, many) -> {
            var name = tuple.x();
            var parser = rules.get(name);
            many.setParserIfNull(parser);
        });
    }

    /**
     * Deletes all information contained in this object.
     */
    public void clear() {
        rules = null;
        placeholders = null;
        manys = null;
    }

    /**
     * Inserts a new named parser into the builder.
     * @param name rule name
     * @param parser parser
     */
    void addParser(String name, Parser<TYPE, ANNOTATION> parser) {
        if (name != null && parser != null) rules.put(name, parser);
    }

    /**
     * If there is no placeholder for this name yet, it will be created first and then returned.
     * Otherwise the placeholder with the name is simply returned.
     * @param name Rule name
     * @return A placeholder parser that matches the passed name.
     */
    PlaceholderParser<TYPE, ANNOTATION> getPlaceholder(String name) {
        if (name == null) return null;
        if (placeholders.containsKey(name)) return placeholders.get(name);

        PlaceholderParser<TYPE, ANNOTATION> placeholder = new PlaceholderParser<>();
        placeholders.put(name, placeholder);
        return placeholder;
    }

    /**
     * If there is no many-parser for this name yet, it will be created first and then returned.
     * Otherwise the many-parser is simply returned with the name.
     * @param name Rule name
     * @return A many-parser that matches the passed name.
     */
    ManyParser<TYPE, ANNOTATION> getMany(TYPE type, String name) {
        if (name == null) return null;
        var tuple = new Tuple<>(name, type);
        if (manys.containsKey(tuple)) return manys.get(tuple);

        ManyParser<TYPE, ANNOTATION> many = new ManyParser<>(type);
        manys.put(tuple, many);
        return many;
    }

    /**
     * Creates a new RuleBuilder with the passed name.
     * @param name Rule name
     * @return Returns the call chain of the RuleBuilder.
     */
    public NewRuleInvocationChain newRule(String name) {
        return new NewRuleInvocationChain(new RuleBuilder<>(name, this));
    }

    /**
     * Puts all the parser in the given pool to the rule map. If there is a name that is already associated
     * with a rule, this rule will be overridden.
     * @param pool ParserPool
     */
    public void union(ParserPool<TYPE, ANNOTATION> pool) {
        rules.putAll(pool.getParsers());
    }

    /**
     *
     * @param regex RegEx
     * @return Returns a pattern based on the given regex.
     * (modifies it if flags are set).
     */
    Pattern getPattern(String regex) {
        return Pattern.compile(regex);
    }


    /**
     * First part of the call chain of a RuleBuilder. This is for readability only and requires
     * the call to consistsOf in order for the rule to be built.
     */
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
