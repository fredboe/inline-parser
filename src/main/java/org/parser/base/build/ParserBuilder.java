package org.parser.base.build;

import org.parser.base.*;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Dient zur Erzeugung eines ParserPools
 * @param <TYPE> Typ für den AST
 * @param <ANNOTATION> Annotation für den AST
 */
public class ParserBuilder<TYPE, ANNOTATION> {
    /**
     * Speichert alle Regeln mit Namen (Eine Regel ist eine Zeile in der BNF).
     */
    private Map<String, Parser<TYPE, ANNOTATION>> rules;
    /**
     * Speichert alle Placeholder mit dem Namen, welche Regel sie repräsentieren sollen.
     * Diese Map muss beim Bauen durchgegangen werden, da die Placeholder davor keinen Parser repräsentieren.
     */
    private Map<String, PlaceholderParser<TYPE, ANNOTATION>> placeholders;
    /**
     * Speichert alle Many-Aufrufe mit dem Namen, welche Regel sie repräsentieren sollen.
     * Diese Map muss beim Bauen durchgegangen werden, da die Placeholder davor keinen Parser repräsentieren.
     */
    private Map<String, ManyParser<TYPE, ANNOTATION>> manys;


    public ParserBuilder() {
        this.rules = new HashMap<>();
        this.placeholders = new HashMap<>();
        this.manys = new HashMap<>();
    }

    /**
     * Baut den ParserPool, der dann alle Regeln als Parser enthält.
     * @return Gibt den ParserPool zurück, der alle Regeln als Parser enthält
     */
    public ParserPool<TYPE, ANNOTATION> build() {
        buildPlaceholders();
        buildManys();
        ParserPool<TYPE, ANNOTATION> pool = new ParserPool<>(rules);
        clear();
        return pool;
    }

    /**
     * Baut die ganzen Placeholder auf, indem die Parser in den Placeholdern durch die zu dem Namen
     * gehörigen Regeln ersetzt werden.
     */
    private void buildPlaceholders() {
        placeholders.forEach((name, placeholder) -> {
            var parser = rules.get(name);
            placeholder.setParserIfNull(parser);
        });
    }

    /**
     * Baut die ganzen Many-Parser auf, indem die Parser in den Many-Parsern durch die zu dem Namen
     * gehörigen Regeln ersetzt werden.
     */
    private void buildManys() {
        manys.forEach((name, placeholder) -> {
            var parser = rules.get(name);
            placeholder.setParserIfNull(parser);
        });
    }

    /**
     * Löscht alle in diesem Objekt enthaltenen Informationen.
     */
    public void clear() {
        rules = null;
        placeholders = null;
        manys = null;
    }

    /**
     * Fügt dem Builder einen neuen benannten Parser ein.
     * @param name Regelname
     * @param parser Parser
     */
    void addParser(String name, Parser<TYPE, ANNOTATION> parser) {
        if (name != null && parser != null) rules.put(name, parser);
    }

    /**
     * Falls es zu diesem Namen noch keinen Placeholder gibt, wird dieser erst erzeugt und dann zurückgegeben.
     * Ansonsten wird der Placeholder mit dem Namen einfach zurückgegeben.
     * @param name Regelname
     * @return Einen Placeholder-Parser, der zu dem übergebenen Namen passt.
     */
    PlaceholderParser<TYPE, ANNOTATION> getPlaceholder(String name) {
        if (name == null) return null;
        if (placeholders.containsKey(name)) return placeholders.get(name);

        PlaceholderParser<TYPE, ANNOTATION> placeholder = new PlaceholderParser<>();
        placeholders.put(name, placeholder);
        return placeholder;
    }

    /**
     * Falls es zu diesem Namen noch keinen Many-Parser gibt, wird dieser erst erzeugt und dann zurückgegeben.
     * Ansonsten wird der Many-Parser mit dem Namen einfach zurückgegeben.
     * @param name Regelname
     * @return Einen Many-Parser, der zu dem übergebenen Namen passt.
     */
    ManyParser<TYPE, ANNOTATION> getMany(TYPE type, String name) {
        if (name == null) return null;
        if (manys.containsKey(name)) return manys.get(name);

        ManyParser<TYPE, ANNOTATION> many = new ManyParser<>(type);
        manys.put(name, many);
        return many;
    }

    /**
     * Erzeugt einen neuen RuleBuilder mit dem übergebenen Namen.
     * @param name Regelname
     * @return Gibt die Aufruf-Kette des RuleBuilders zurück.
     */
    public NewRuleInvocationChain newRule(String name) {
        return new NewRuleInvocationChain(new RuleBuilder<>(name, this));
    }

    /**
     *
     * @param regex RegEx
     * @return Gibt ein Pattern basierend auf der übergebenen RegEx zurück
     *         (verändert diese, falls Flags gesetzt wurden).
     */
    Pattern getPattern(String regex) {
        return Pattern.compile(regex);
    }


    /**
     * Erster Teil der Aufruf-Kette eines RuleBuilders. Dieser dient nur der Lesbarkeit und erfordert
     * den Aufruf von consistsOf, damit die Regel gebaut werden kann.
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
