package org.parser.base.build;

import org.parser.Tuple;
import org.parser.base.*;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Used to create a ParserPool.
 * @param <TYPE> type for the AST
 */
public class ParserBuilder<TYPE> {
    /**
     * Stores all rules by name (A rule is one line in the BNF).
     */
    private Map<String, Parser<TYPE>> rules;
    /**
     * Stores all placeholders with the name of the rule they should represent.
     * This map must be iterated through when building, as the placeholders before build do not represent a parser.
     */
    private Map<String, PlaceholderParser<TYPE>> placeholders;
    /**
     * Stores all many calls with the name of which rule they should represent.
     * This map must be iterated through when building, as the manys before build do not represent a parser.
     */
    private Map<Tuple<String, TYPE>, ManyParser<TYPE>> manys;


    public ParserBuilder() {
        this.rules = new HashMap<>();
        this.placeholders = new HashMap<>();
        this.manys = new HashMap<>();
    }

    /**
     * Builds the ParserPool, which then contains all rules as parsers.
     * @return Returns the ParserPool which contains all rules as parser.
     */
    public ParserPool<TYPE> build() {
        buildPlaceholders();
        buildManys();
        ParserPool<TYPE> pool = new ParserPool<>(rules);
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
        rules = new HashMap<>();
        placeholders = new HashMap<>();
        manys = new HashMap<>();
    }

    /**
     * Inserts a new named parser into the builder.
     * @param name rule name
     * @param parser parser
     */
    void addParser(String name, Parser<TYPE> parser) {
        if (name != null && parser != null) rules.put(name, parser);
    }

    /**
     * If there is no placeholder for this name yet, it will be created first and then returned.
     * Otherwise the placeholder with the name is simply returned.
     * @param name Rule name
     * @return A placeholder parser that matches the passed name.
     */
    PlaceholderParser<TYPE> getPlaceholder(String name) {
        if (name == null) return null;
        if (placeholders.containsKey(name)) return placeholders.get(name);

        PlaceholderParser<TYPE> placeholder = new PlaceholderParser<>();
        placeholders.put(name, placeholder);
        return placeholder;
    }

    /**
     * If there is no many-parser for this name yet, it will be created first and then returned.
     * Otherwise the many-parser is simply returned with the name.
     * @param name Rule name
     * @return A many-parser that matches the passed name.
     */
    ManyParser<TYPE> getMany(TYPE type, String name) {
        if (name == null) return null;
        var tuple = new Tuple<>(name, type);
        if (manys.containsKey(tuple)) return manys.get(tuple);

        ManyParser<TYPE> many = new ManyParser<>(type);
        manys.put(tuple, many);
        return many;
    }

    /**
     * Creates a new RuleBuilder with the passed name.
     * @param name Rule name
     * @return Returns the call chain of the RuleBuilder.
     */
    public RuleBuilder<TYPE> newRule(String name) {
        return new RuleBuilder<>(name, this);
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

    public void union(ParserBuilder<TYPE> other) {
        rules.putAll(other.rules);
        placeholders.putAll(other.placeholders);
        manys.putAll(other.manys);
    }

    public static <T> ParserBuilder<T> union(ParserBuilder<T> builder1, ParserBuilder<T> builder2) {
        var resBuilder = new ParserBuilder<T>();
        resBuilder.rules.putAll(builder1.rules);
        resBuilder.rules.putAll(builder2.rules);
        resBuilder.placeholders.putAll(builder1.placeholders);
        resBuilder.placeholders.putAll(builder2.placeholders);
        resBuilder.manys.putAll(builder1.manys);
        resBuilder.manys.putAll(builder2.manys);

        return resBuilder;
    }
}
