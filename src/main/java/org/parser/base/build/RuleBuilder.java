package org.parser.base.build;

import org.parser.Consumable;
import org.parser.base.OrParser;
import org.parser.base.Parser;
import org.parser.base.RegExParser;
import org.parser.tree.AST;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * Der Rule-Builder ermöglicht das Bauen von Regeln (also einer Zeile in der BNF).
 * @param <TYPE> Typ des AST
 * @param <ANNOTATION> Annotation des AST
 */
public class RuleBuilder<TYPE, ANNOTATION> {
    /**
     * Regelname (mit diesem wird man auf sie zugreifen können)
     */
    private final String ruleName;
    /**
     * Der zugrundeliegende ParserBuilder. Dieser wird für die rule-Methode verwendet, damit ihm ein
     * Placeholder hinzugefügt werden kann und für das Erzeugen der ConcatRuleBuilder und NextIsOrBuilder.
     */
    private final ParserBuilder<TYPE, ANNOTATION> parserBuilder;
    /**
     * Die letztendliche Regel
     */
    private final OrParser<TYPE, ANNOTATION> rule;
    /**
     * Gibt an, ob der RuleBuilder noch modifizierbar ist oder nicht.
     */
    private boolean frozen;

    /**
     * Der nextIsOrBuilder (dieser kann immer wieder verwendet werden und muss nicht immer neu erzeugt werden
     * oder zurückgesetzt werden)
     */
    private final NextIsOrBuilder<TYPE, ANNOTATION> nextIsOrBuilder;
    /**
     * Der ConcatRuleBuilder (dieser kann immer wieder verwendet werden und muss nicht immer neu erzeugt werden,
     * denn er kann einfach zurückgesetzt werden)
     */
    private final ConcatRuleBuilder<TYPE, ANNOTATION> concatRuleBuilder;

    public RuleBuilder(String ruleName, ParserBuilder<TYPE, ANNOTATION> parserBuilder) {
        this.ruleName = ruleName;
        this.parserBuilder = parserBuilder;
        this.rule = Parser.or(new ArrayList<>());
        this.nextIsOrBuilder = new NextIsOrBuilder<>(parserBuilder, this);
        this.concatRuleBuilder = new ConcatRuleBuilder<>(parserBuilder, this);
        this.frozen = false;
    }

    /**
     * Erzeugt eine neue Subrule, die aus mehreren Elementen besteht.
     * @param atSuccess Wird aufgerufen, wenn der ConcatParser erfolgreich ist.
     * @return Ein zurückgesetzter ConcatRuleBuilder, dessen ConcatParser die übergebene atSuccess-Methode verwendet.
     */
    public ConcatRuleBuilder<TYPE, ANNOTATION> concat(Function<List<AST<TYPE, ANNOTATION>>, AST<TYPE, ANNOTATION>> atSuccess) {
        if (!frozen) concatRuleBuilder.newSubrule(atSuccess);
        return concatRuleBuilder;
    }

    /**
     * Erzeugt eine neue Subrule, die aus mehreren Elementen besteht.
     * @return Ein zurückgesetzter ConcatRuleBuilder, dessen ConcatParser als AST einfach den AST des ersten Kindes nimmt.
     */
    public ConcatRuleBuilder<TYPE, ANNOTATION> concat() {
        return concat(
                trees -> trees.size() >= 1
                        ? trees.get(0)
                        : null
        );
    }

    /**
     * Erzeugt eine neue Subrule, die aus mehreren Elementen besteht.
     * @param type Typ des AST
     * @return Ein zurückgesetzter ConcatRuleBuilder, dessen ConcatParser als AST den übergebenen Typen hat und
     *         als Kinder einfach die ASTs der Elemente der Subrule hat.
     */
    public ConcatRuleBuilder<TYPE, ANNOTATION> concat(TYPE type) {
        return concat(Parser.basicConcatAtSuccess(type));
    }

    /**
     * Erzeugt als neue Klausel einen match-Parser (für mehrelementige Klauseln muss zunächst concat aufgerufen werden).
     * @param type Typ des AST
     * @param pattern Pattern
     * @return Gibt den NextIsOrBuilder zurück.
     */
    public NextIsOrBuilder<TYPE, ANNOTATION> match(TYPE type, Pattern pattern) {
        return addSingleClause(Parser.match(type, pattern));
    }

    /**
     * Erzeugt als neue Klausel einen match-Parser (für mehrelementige Klauseln muss zunächst concat aufgerufen werden).
     * @param type Typ des AST
     * @param regex RegEx
     * @return Gibt den NextIsOrBuilder zurück.
     */
    public NextIsOrBuilder<TYPE, ANNOTATION> match(TYPE type, String regex) {
        return match(type, parserBuilder.getPattern(regex));
    }

    /**
     * Erzeugt als neue Klausel einen keyword-Parser (für mehrelementige Klauseln muss zunächst concat aufgerufen werden).
     * @param type Typ des AST
     * @param pattern Pattern
     * @return Gibt den NextIsOrBuilder zurück.
     */
    public NextIsOrBuilder<TYPE, ANNOTATION> keyword(TYPE type, Pattern pattern) {
        return addSingleClause(Parser.keyword(type, pattern));
    }

    /**
     * Erzeugt als neue Klausel einen keyword-Parser (für mehrelementige Klauseln muss zunächst concat aufgerufen werden).
     * @param type Typ des AST
     * @param regex RegEx
     * @return Gibt den NextIsOrBuilder zurück.
     */
    public NextIsOrBuilder<TYPE, ANNOTATION> keyword(TYPE type, String regex) {
        return keyword(type, parserBuilder.getPattern(regex));
    }

    /**
     * Erzeugt als neue Klausel einen customRegEx-Parser (für mehrelementige Klauseln muss zunächst concat aufgerufen werden).
     * @param atSuccess Wird beim Erfolg des RegEx-Parsers aufgerufen
     * @param pattern Pattern
     * @return Gibt den NextIsOrBuilder zurück.
     */
    public NextIsOrBuilder<TYPE, ANNOTATION> customRegEx(Function<Consumable.Match, AST<TYPE, ANNOTATION>> atSuccess,
                                                         Pattern pattern) {
        return addSingleClause(new RegExParser<>(pattern, atSuccess));
    }

    /**
     * Erzeugt als neue Klausel einen customRegEx-Parser (für mehrelementige Klauseln muss zunächst concat aufgerufen werden).
     * @param atSuccess Wird beim Erfolg des RegEx-Parsers aufgerufen
     * @param regex RegEx
     * @return Gibt den NextIsOrBuilder zurück.
     */
    public NextIsOrBuilder<TYPE, ANNOTATION> customRegEx(Function<Consumable.Match, AST<TYPE, ANNOTATION>> atSuccess,
                                                         String regex) {
        return customRegEx(atSuccess, parserBuilder.getPattern(regex));
    }

    /**
     * Erzeugt als neue Klausel einen Placeholder-Parser, der die Regel mit dem übergebenen Namen abbilden wird, ein.
     * @param name Regelname
     * @return Gibt den NextIsOrBuilder zurück.
     */
    public NextIsOrBuilder<TYPE, ANNOTATION> rule(String name) {
        return addSingleClause(parserBuilder.getPlaceholder(name));
    }

    /**
     * Erzeugt als neue Klausel einen Many-Parser, der die Regel mit dem übergebenen Namen abbilden wird, ein.
     * @param type Typ zu dem der Many-Ausdruck zusammengefasst werden soll
     * @param name Regelname
     * @return Gibt den NextIsOrBuilder zurück.
     */
    public NextIsOrBuilder<TYPE, ANNOTATION> many(TYPE type, String name) {
        return addSingleClause(parserBuilder.getMany(type, name));
    }

    /**
     * Erzeugt als neue Klausel einen Some-Parser, der die Regel mit dem übergebenen Namen abbilden wird, ein.
     * @param type Typ zu dem der Some-Ausdruck zusammengefasst werden soll
     * @param name Regelname
     * @return Gibt den NextIsOrBuilder zurück.
     */
    public NextIsOrBuilder<TYPE, ANNOTATION> some(TYPE type, String name) {
        return addSingleClause(parserBuilder.getSome(type, name));
    }

    /**
     *
     * @return Gibt den Regelnamen zurück
     */
    public String getName() {
        return ruleName;
    }

    /**
     * Fügt als neue Klausel einen einelementigen Parser ein
     * @param singleParser einelementiger Parser
     * @return Gibt den NextIsOrBuilder zurück.
     */
    private NextIsOrBuilder<TYPE, ANNOTATION> addSingleClause(Parser<TYPE, ANNOTATION> singleParser) {
        addClause(singleParser);
        return nextIsOrBuilder;
    }

    /**
     * Fügt den übergebenen Parser als neue Klausel ein.
     * @param parser Parser
     */
    void addClause(Parser<TYPE, ANNOTATION> parser) {
        if (!frozen) rule.addSubparser(parser);
    }

    /**
     * Friert den RuleBuilder ein, sodass Methodenaufrufe nichts mehr bewirken und gibt den entstandenen
     * Parser des RuleBuilders zurück.
     * @return Der Parser, der aus dem RuleBuilder entsteht.
     */
    Parser<TYPE, ANNOTATION> freeze() {
        frozen = true;
        return rule;
    }
}
