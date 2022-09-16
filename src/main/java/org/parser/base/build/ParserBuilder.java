package org.parser.base.build;

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

    public ParserBuilder() {
        this.rules = new HashMap<>();
        this.placeholders = new HashMap<>();
    }

    /**
     * Builds the ParserPool, which then contains all rules as parsers.
     * @return Returns the ParserPool which contains all rules as parser.
     */
    public ParserPool<TYPE> build() {
        buildPlaceholders();
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
     * Deletes all information contained in this object.
     */
    public void clear() {
        rules = new HashMap<>();
        placeholders = new HashMap<>();
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
     * Creates a new RuleBuilder with the passed name.
     * @param name Rule name
     * @return Returns the call chain of the RuleBuilder.
     */
    public Rule<TYPE> newRule(String name) {
        return new Rule<>(name, this);
    }

    public void unite(ParserBuilder<TYPE> other) {
        rules.putAll(other.rules);
        other.placeholders.forEach((name, placeholder) -> {
            if (this.placeholders.containsKey(name)) {
                placeholder.setParserIfNull(this.placeholders.get(name));
            } else {
                this.placeholders.put(name, placeholder);
            }
        });
    }

    public static <T> ParserBuilder<T> unite(ParserBuilder<T> builder1, ParserBuilder<T> builder2) {
        var resBuilder = new ParserBuilder<T>();
        resBuilder.unite(builder1);
        resBuilder.unite(builder2);
        return resBuilder;
    }
}
