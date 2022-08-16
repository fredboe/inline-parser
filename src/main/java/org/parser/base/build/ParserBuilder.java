package org.parser.base.build;

import org.parser.Consumable;
import org.parser.base.*;
import org.parser.tree.AST;

import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * Dient zur Erzeugung eines ParserPools
 * @param <TYPE> Typ für den AST
 * @param <ANNOTATION> Annotation für den AST
 */
public class ParserBuilder<TYPE, ANNOTATION> {
    /**
     * Alle Parser, auf die nach dem build zugegriffen werden kann (mit Namen)
     */
    private Map<String, Parser<TYPE, ANNOTATION>> referencedParsers;

    /**
     * Alle Parser, die in sich weitere Parser speichern (Or oder Concat), gespeichert mit der
     * Liste der Namen der Subparser. Diese Map existiert, da beim build alle Subparser (mit dem Namen)
     * aktualisiert werden müssen.
     */
    private Map<WithSubparsersParser<TYPE, ANNOTATION>, List<String>> parsersWithSubparsers;

    /**
     * Alle Parser, die beim build nicht explizit gespeichert werden sollen
     */
    private Map<String, Parser<TYPE, ANNOTATION>> temporaryParsers;

    public ParserBuilder() {
        referencedParsers = new HashMap<>();
        parsersWithSubparsers = new HashMap<>();
        temporaryParsers = new HashMap<>();
    }

    /**
     * Erzeugt aus den übergebenen Informationen einen ParserPool, in diesem sind alle
     * Parser enthalten, die nicht mit temporary hinzugefügt wurden.
     * @return Gibt einen aus dem ParserBuilder erzeugten ParserPool zurück
     */
    public ParserPool<TYPE, ANNOTATION> build() {
        parsersWithSubparsers.forEach((parser, subparserList) ->
                parser.setSubparsers(convertParserNamesToParserList(subparserList)));
        return new ParserPool<>(referencedParsers);
    }

    /**
     * Löscht alle in diesem ParserBuilder enthaltenen Daten
     */
    public void clear() {
        referencedParsers = new HashMap<>(0);
        parsersWithSubparsers = new HashMap<>(0);
        temporaryParsers = new HashMap<>(0);
    }


    /**
     * Fügt dem Builder einen grundlegenden Match-Parser hinzu. Dieser ruft die Erfolgsmethode auf,
     * wenn das übergebene Pattern erfolgreich gematcht werden konnte. Die Erfolgsmethode gibt einen AST zurück
     * mit dem Typ type und dem gematchten Match.
     * @param name Name des Parsers
     * @param type Typ
     * @param pattern Pattern
     * @return Den zugrundeliegenden Parser-Builder
     */
    public ParserBuilder<TYPE, ANNOTATION> addMatchParser(String name, TYPE type, Pattern pattern) {
        return addParser(name, match(type, pattern));
    }

    /**
     * Fügt dem Builder einen grundlegenden Match-Parser hinzu. Dieser ruft die Erfolgsmethode auf,
     * wenn das übergebene Pattern erfolgreich gematcht werden konnte. Die Erfolgsmethode gibt einen AST zurück
     * mit dem Typ type und dem gematchten Match.
     * @param name Name des Parsers
     * @param type Typ
     * @param regex Regular-Expression
     * @return Den zugrundeliegenden Parser-Builder
     */
    public ParserBuilder<TYPE, ANNOTATION> addMatchParser(String name, TYPE type, String regex) {
        return addParser(name, match(type, regex));
    }

    /**
     * Fügt dem Builder einen grundlegenden Keyword-Parser hinzu. Dieser ruft die Erfolgsmethode auf,
     * wenn das übergebene Pattern erfolgreich gematcht werden konnte. Die Erfolgsmethode gibt einen AST zurück,
     * mit dem Typ type, bei dem das Match-Objekt auf null gesetzt ist.
     * @param name Name des Parsers
     * @param type Typ
     * @param pattern Pattern
     * @return Den zugrundeliegenden Parser-Builder
     */
    public ParserBuilder<TYPE, ANNOTATION> addKeywordParser(String name, TYPE type, Pattern pattern) {
        return addParser(name, keyword(type, pattern));
    }

    /**
     * Fügt dem Builder einen grundlegenden Keyword-Parser hinzu. Dieser ruft die Erfolgsmethode auf,
     * wenn das übergebene Pattern erfolgreich gematcht werden konnte. Die Erfolgsmethode gibt einen AST zurück,
     * mit dem Typ type, bei dem das Match-Objekt auf null gesetzt ist.
     * @param name Name des Parsers
     * @param type Typ
     * @param regex Regular-Expression
     * @return Den zugrundeliegenden Parser-Builder
     */
    public ParserBuilder<TYPE, ANNOTATION> addKeywordParser(String name, TYPE type, String regex) {
        return addParser(name, keyword(type, regex));
    }

    /**
     * Fügt dem Builder einen grundlegenden Hide-Parser hinzu. Dieser ruft die Erfolgsmethode auf,
     * wenn das übergebene Pattern erfolgreich gematcht werden konnte. Die Erfolgsmethode gibt einfach einen AST zurück,
     * mit dem Typ type, bei dem das ignore-Bit gesetzt ist.
     * @param name Name des Parsers
     * @param type Typ
     * @param pattern Pattern
     * @return Den zugrundeliegenden Parser-Builder
     */
    public ParserBuilder<TYPE, ANNOTATION> addHideParser(String name, TYPE type, Pattern pattern) {
        return addParser(name, hide(type, pattern));
    }

    /**
     * Fügt dem Builder einen grundlegenden Hide-Parser hinzu. Dieser ruft die Erfolgsmethode auf,
     * wenn das übergebene Pattern erfolgreich gematcht werden konnte. Die Erfolgsmethode gibt einfach einen AST zurück,
     * mit dem Typ type, bei dem das ignore-Bit gesetzt ist.
     * @param name Name des Parsers
     * @param type Typ
     * @param regex Regular-Expression
     * @return Den zugrundeliegenden Parser-Builder
     */
    public ParserBuilder<TYPE, ANNOTATION> addHideParser(String name, TYPE type, String regex) {
        return addParser(name, hide(type, regex));
    }

    /**
     * Fügt dem Builder einen Custom RegExParser hinzu. Dieser führt die atSuccess Methode aus, wenn
     * die übergebene RegEx mit dem Anfang des Consumable übereinstimmt.
     * @param name Name des Parsers
     * @param atSuccess Funktion, die beim erfolgreichen Match ausgeführt wird
     * @param pattern Pattern
     * @return Den zugrundeliegenden Parser-Builder
     */
    public ParserBuilder<TYPE, ANNOTATION> addRegExParser(String name, Pattern pattern,
                                                          Function<Consumable.Match, AST<TYPE, ANNOTATION>> atSuccess) {
        return addParser(name, new RegExParser<>(pattern, atSuccess));
    }

    /**
     * Fügt dem Builder einen Custom RegEx-Parser hinzu. Dieser führt die atSuccess Methode aus, wenn
     * die übergebene RegEx mit dem Anfang des Consumable übereinstimmt.
     * @param name Name des Parsers
     * @param atSuccess Funktion, die beim erfolgreichen Match ausgeführt wird
     * @param regex Regular-Expression
     * @return Den zugrundeliegenden Parser-Builder
     */
    public ParserBuilder<TYPE, ANNOTATION> addRegExParser(String name, String regex,
                                                          Function<Consumable.Match, AST<TYPE, ANNOTATION>> atSuccess) {
        return addRegExParser(name, Pattern.compile(regex), atSuccess);
    }

    /**
     * Fügt dem Builder einen Or-Parser hinzu. Dieser führt die Erfolgsmethode aus, wenn einer der Subparser erfolgreich
     * war.
     * @param name Name des Parsers
     * @param atSuccess Methode, die beim Erfolg ausgeführt wird
     * @param parserNames Namen der Subparser
     * @return Den zugrundeliegenden Parser-Builder
     */
    public ParserBuilder<TYPE, ANNOTATION> addOr(String name, Function<AST<TYPE, ANNOTATION>, AST<TYPE, ANNOTATION>> atSuccess,
                                                 String ... parserNames) {
        OrParser<TYPE, ANNOTATION> orParser = new OrParser<>(atSuccess);
        return addWithSubparsers(name, orParser, Arrays.asList(parserNames));
    }

    /**
     * Fügt dem Builder einen Or-Parser hinzu. Seine Erfolgsfunktion ist einfach die Identitätsfunktion.
     * @param name Name des Parsers
     * @param parserNames Namen der Subparser
     * @return Den zugrundeliegenden Parser-Builder
     */
    public ParserBuilder<TYPE, ANNOTATION> addOr(String name, String ... parserNames) {
        return addOr(name, ast -> ast, parserNames);
    }

    public ParserBuilder<TYPE, ANNOTATION> addTemporaryOr(String name, Function<AST<TYPE, ANNOTATION>, AST<TYPE, ANNOTATION>> atSuccess,
                                                 String ... parserNames) {
        OrParser<TYPE, ANNOTATION> orParser = new OrParser<>(atSuccess);
        return addTemporaryWithSubparsers(name, orParser, Arrays.asList(parserNames));
    }

    public ParserBuilder<TYPE, ANNOTATION> addTemporaryOr(String name, String ... parserNames) {
        return addTemporaryOr(name, ast -> ast, parserNames);
    }

    /**
     * Fügt dem Builder einen Or-Parser hinzu. Seine Erfolgsfunktion ist einfach die erzeugt einen neuen AST-Knoten
     * mit dem Typen type und fügt diesem als einziges Kind den übergebenen AST ein.
     * @param name Name des Parsers
     * @param parserNames Namen der Subparser
     * @return Den zugrundeliegenden Parser-Builder
     */
    public ParserBuilder<TYPE, ANNOTATION> addOrWithNode(String name, TYPE type, String ... parserNames) {
        return addOr(name, ast -> new AST<TYPE, ANNOTATION>(type).addChild(ast), parserNames);
    }

    /**
     * Fügt dem Builder einen Concat-Parser hinzu. Seine Erfolgsfunktion wird ausgeführt, wenn alle
     * Subparser erfolgreich waren.
     * @param name Name des Parsers
     * @param atSuccess Funktion, die beim Erfolg ausgeführt wird
     * @param parserNames Namen der Subparser
     * @return Den zugrundeliegenden Parser-Builder
     */
    public ParserBuilder<TYPE, ANNOTATION> addConcat(String name, Function<List<AST<TYPE, ANNOTATION>>, AST<TYPE, ANNOTATION>> atSuccess,
                                                     String ... parserNames) {
        ConcatParser<TYPE, ANNOTATION> concatParser = new ConcatParser<>(atSuccess);
        return addWithSubparsers(name, concatParser, Arrays.asList(parserNames));
    }

    /**
     * Fügt dem Builder einen Concat-Parser hinzu. Seine Erfolgsfunktion erzeugt einfach einen neuen
     * AST-Knoten, der den Typen type hat und als Kindknoten gerade die erzeugten ASTs der Subparser.
     * @param name Name des Parsers
     * @param parserNames Namen der Subparser
     * @return Den zugrundeliegenden Parser-Builder
     */
    public ParserBuilder<TYPE, ANNOTATION> addConcat(String name, TYPE type, String ... parserNames) {
        return addConcat(name, trees -> new AST<>(type, null, trees), parserNames);
    }

    public ParserBuilder<TYPE, ANNOTATION> addTemporaryConcat(String name, Function<List<AST<TYPE, ANNOTATION>>, AST<TYPE, ANNOTATION>> atSuccess,
                                                     String ... parserNames) {
        ConcatParser<TYPE, ANNOTATION> concatParser = new ConcatParser<>(atSuccess);
        return addTemporaryWithSubparsers(name, concatParser, Arrays.asList(parserNames));
    }

    public ParserBuilder<TYPE, ANNOTATION> addTemporaryConcat(String name, TYPE type, String ... parserNames) {
        return addTemporaryConcat(name, trees -> new AST<>(type, null, trees), parserNames);
    }

    /**
     * Fügt in die referencedParsers Map einen neuen Parser mit dem Namen name ein
     * @param name Name des Parsers
     * @param parser Parser
     * @return Den zugrundeliegenden Parser-Builder
     */
    private ParserBuilder<TYPE, ANNOTATION> addParser(String name, Parser<TYPE, ANNOTATION> parser) {
        referencedParsers.put(name, parser);
        return this;
    }

    private ParserBuilder<TYPE, ANNOTATION> addTemporaryParser(String name, Parser<TYPE, ANNOTATION> parser) {
        temporaryParsers.put(name, parser);
        return this;
    }

    /**
     * Fügt einen Parser ein, der Subparser besitzt (diese benötigen beim Bauen Sonderbehandlung,
     * entweder concat oder or).
     * @param name Name des Parsers
     * @param parser Parser
     * @param subparserNames Namen der Subparser
     * @return  Den zugrundeliegenden Parser-Builder
     */
    private ParserBuilder<TYPE, ANNOTATION> addWithSubparsers(String name, WithSubparsersParser<TYPE, ANNOTATION> parser, List<String> subparserNames) {
        parsersWithSubparsers.put(parser, subparserNames);
        return addParser(name, parser);
    }

    private ParserBuilder<TYPE, ANNOTATION> addTemporaryWithSubparsers(String name, WithSubparsersParser<TYPE, ANNOTATION> parser, List<String> subparserNames) {
        parsersWithSubparsers.put(parser, subparserNames);
        return addTemporaryParser(name, parser);
    }

    /**
     * Konvertiert eine Liste von Parser-Namen zu einer Liste von Parsern, indem die Namen zunächst in
     * der referencedMap gesucht werden und anschließend in der temporaryMap. Nicht gefundene Namen werden
     * einfach ignoriert.
     * @param parserNames Namen der Parser
     * @return Gibt eine Liste von Parsern zurück
     */
    private List<Parser<TYPE, ANNOTATION>> convertParserNamesToParserList(List<String> parserNames) {
        return parserNames.stream()
                .filter(name -> referencedParsers.containsKey(name) || temporaryParsers.containsKey(name))
                .map(name -> referencedParsers.containsKey(name) ? referencedParsers.get(name) : temporaryParsers.get(name))
                .toList();
    }

    /**
     * Ein grundlegender Hide-Parser. Dieser ruft die Erfolgsmethode auf, wenn das übergebene Pattern erfolgreich gematcht
     * werden konnte. Die Erfolgsmethode gibt einfach einen AST zurück, mit dem Typ type, bei dem das ignore-Bit gesetzt ist.
     * @param type Typ
     * @param pattern Pattern
     * @return Ein grundlegender Hide-Parser
     */
    private RegExParser<TYPE, ANNOTATION> hide(TYPE type, Pattern pattern) {
        return new RegExParser<>(pattern, match -> new AST<TYPE, ANNOTATION>(type).setIgnore(true));
    }

    /**
     * Ein grundlegender Hide-Parser. Dieser ruft die Erfolgsmethode auf, wenn das übergebene Pattern erfolgreich gematcht
     * werden konnte. Die Erfolgsmethode gibt einfach einen AST zurück, mit dem Typ type, bei dem das ignore-Bit gesetzt ist.
     * @param type Typ
     * @param regex Regular-Expression
     * @return Ein grundlegender Hide-Parser
     */
    private RegExParser<TYPE, ANNOTATION> hide(TYPE type, String regex) {
        return hide(type, Pattern.compile(regex));
    }

    /**
     * Ein grundlegender Keyword-Parser. Dieser ruft die Erfolgsmethode auf, wenn das übergebene Pattern erfolgreich gematcht
     * werden konnte. Die Erfolgsmethode gibt einen AST zurück mit dem Typ type, bei dem das Match-Objekt auf null gesetzt ist.
     * @param type Typ
     * @param pattern Pattern
     * @return Ein grundlegender Keyword-Parser
     */
    private RegExParser<TYPE, ANNOTATION> keyword(TYPE type, Pattern pattern) {
        return new RegExParser<>(pattern, match -> new AST<>(type, null));
    }

    /**
     * Ein grundlegender Keyword-Parser. Dieser ruft die Erfolgsmethode auf, wenn das übergebene Pattern erfolgreich gematcht
     * werden konnte. Die Erfolgsmethode gibt einen AST zurück mit dem Typ type, bei dem das Match-Objekt auf null gesetzt ist.
     * @param type Typ
     * @param regex Regular-Expression
     * @return Ein grundlegender Keyword-Parser
     */
    private RegExParser<TYPE, ANNOTATION> keyword(TYPE type, String regex) {
        return keyword(type, Pattern.compile(regex));
    }

    /**
     * Ein grundlegender Match-Parser. Dieser ruft die Erfolgsmethode auf, wenn das übergebene Pattern erfolgreich gematcht
     * werden konnte. Die Erfolgsmethode gibt einen AST zurück mit dem Typ type und dem gematchten Match.
     * @param type Typ
     * @param pattern Pattern
     * @return Ein grundlegender Match-Parser
     */
    private RegExParser<TYPE, ANNOTATION> match(TYPE type, Pattern pattern) {
        return new RegExParser<>(pattern, match -> new AST<>(type, match));
    }

    /**
     * Ein grundlegender Match-Parser. Dieser ruft die Erfolgsmethode auf, wenn das übergebene Pattern erfolgreich gematcht
     * werden konnte. Die Erfolgsmethode gibt einen AST zurück mit dem Typ type und dem gematchten Match.
     * @param type Typ
     * @param regex Regular-Expression
     * @return Ein grundlegender Match-Parser
     */
    private RegExParser<TYPE, ANNOTATION> match(TYPE type, String regex) {
        return match(type, Pattern.compile(regex));
    }
}
