package org.parser.base;

import org.parser.Consumable;
import org.parser.tree.AST;

import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Regeln:
 * - Ein Parser sollte das Consumable nur konsumieren, wenn der Parser erfolgreich ist.
 * - Wird ein Verbindungsparser (ASTs als Input) implementiert, sollten die ASTs ignoriert werden,
 *   bei denen das ignore-Bit gesetzt ist
 * - Ein Parsing-Fehler soll mittels Optional übergeben werden (empty)
 * @param <TYPE> Typ
 * @param <ANNOTATION> ANNOTATION-Class beim Abstract Syntax Tree
 */
public interface Parser<TYPE, ANNOTATION> {
    /**
     * Erhält eine CharSequence und erzeugt aus dieser einen AST.
     * @param consumable Consumable
     * @return Ein AST mit Optional gewrappt (empty falls Parsing-Fehler)
     */
    Optional<AST<TYPE, ANNOTATION>> applyTo(Consumable consumable);

    /**
     * Grundlegender Or-Parser. Dieser ruft die Erfolgsmethode auf, wenn einer der Parser erfolgreich war.
     * Ist kein Parser erfolgreich, wird Optional.emtpy() zurückgegeben.Die Erfolgsmethode erzeugt einen neuen AST
     * mit dem Typen type und fügt den übergebenen AST als Kind zu.
     * @param type Typ
     * @param parsers Parser, die verodert werden
     * @return Einen grundlegenden Or-Parser
     * @param <TYPE> Typ
     * @param <ANNOTATION> ANNOTATION-Class beim Abstract Syntax Tree
     */
    @SafeVarargs
    static <TYPE, ANNOTATION> Parser<TYPE, ANNOTATION> or(TYPE type, Parser<TYPE, ANNOTATION> ... parsers) {
        return new OrParser<>(ast -> new AST<TYPE, ANNOTATION>(type).addChild(ast), parsers);
    }

    /**
     * Ein grundlegender Concatenation-Parser. Dieser ruft die Erfolgsmethode auf, wenn alle übergebenen Parser
     * erfolgreich waren. Die Erfolgsmethode gibt einfach einen AST zurück mit dem Typ type, der alle übergebenen ASTs
     * als Kinder enthält.
     * @param type Typ
     * @param parsers Parser, die verknüpft werden sollen.
     * @return Ein grundlegender Concatenation-Parser.
     * @param <TYPE> Typ
     * @param <ANNOTATION> ANNOTATION-Class beim Abstract Syntax Tree
     */
    @SafeVarargs
    static <TYPE, ANNOTATION> Parser<TYPE, ANNOTATION> concat(TYPE type, Parser<TYPE, ANNOTATION> ... parsers) {
        return new ConcatParser<>(trees -> new AST<>(type, null, trees), parsers);
    }

    /**
     * Ein grundlegender Hide-Parser. Dieser ruft die Erfolgsmethode auf, wenn das übergebene Pattern erfolgreich gematcht
     * werden konnte. Die Erfolgsmethode gibt einfach einen AST zurück, mit dem Typ type, bei dem das ignore-Bit gesetzt ist.
     * @param type Typ
     * @param pattern Pattern
     * @return Ein grundlegender Hide-Parser
     * @param <TYPE> Typ
     * @param <ANNOTATIONS> ANNOTATION-Class beim Abstract Syntax Tree
     */
    static <TYPE, ANNOTATIONS> Parser<TYPE, ANNOTATIONS> hide(TYPE type, Pattern pattern) {
        return new RegExParser<>(pattern, match -> new AST<TYPE, ANNOTATIONS>(type).setIgnore(true));
    }

    /**
     * Ein grundlegender Hide-Parser. Dieser ruft die Erfolgsmethode auf, wenn das übergebene Pattern erfolgreich gematcht
     * werden konnte. Die Erfolgsmethode gibt einfach einen AST zurück, mit dem Typ type, bei dem das ignore-Bit gesetzt ist.
     * @param type Typ
     * @param regex Regular-Expression
     * @return Ein grundlegender Hide-Parser
     * @param <TYPE> Typ
     * @param <ANNOTATIONS> ANNOTATION-Class beim Abstract Syntax Tree
     */
    static <TYPE, ANNOTATIONS> Parser<TYPE, ANNOTATIONS> hide(TYPE type, String regex) {
        return hide(type, Pattern.compile(regex));
    }

    /**
     * Ein grundlegender Keyword-Parser. Dieser ruft die Erfolgsmethode auf, wenn das übergebene Pattern erfolgreich gematcht
     * werden konnte. Die Erfolgsmethode gibt einen AST zurück mit dem Typ type, bei dem das Match-Objekt auf null gesetzt ist.
     * @param type Typ
     * @param pattern Pattern
     * @return Ein grundlegender Keyword-Parser
     * @param <TYPE> Typ
     * @param <ANNOTATIONS> ANNOTATION-Class beim Abstract Syntax Tree
     */
    static <TYPE, ANNOTATIONS> Parser<TYPE, ANNOTATIONS> keyword(TYPE type, Pattern pattern) {
        return new RegExParser<>(pattern, match -> new AST<>(type, null));
    }

    /**
     * Ein grundlegender Keyword-Parser. Dieser ruft die Erfolgsmethode auf, wenn das übergebene Pattern erfolgreich gematcht
     * werden konnte. Die Erfolgsmethode gibt einen AST zurück mit dem Typ type, bei dem das Match-Objekt auf null gesetzt ist.
     * @param type Typ
     * @param regex Regular-Expression
     * @return Ein grundlegender Keyword-Parser
     * @param <TYPE> Typ
     * @param <ANNOTATIONS> ANNOTATION-Class beim Abstract Syntax Tree
     */
    static <TYPE, ANNOTATIONS> Parser<TYPE, ANNOTATIONS> keyword(TYPE type, String regex) {
        return keyword(type, Pattern.compile(regex));
    }

    /**
     * Ein grundlegender Match-Parser. Dieser ruft die Erfolgsmethode auf, wenn das übergebene Pattern erfolgreich gematcht
     * werden konnte. Die Erfolgsmethode gibt einen AST zurück mit dem Typ type und dem gematchten Match.
     * @param type Typ
     * @param pattern Pattern
     * @return Ein grundlegender Match-Parser
     * @param <TYPE> Typ
     * @param <ANNOTATIONS> ANNOTATION-Class beim Abstract Syntax Tree
     */
    static <TYPE, ANNOTATIONS> Parser<TYPE, ANNOTATIONS> match(TYPE type, Pattern pattern) {
        return new RegExParser<>(pattern, match -> new AST<>(type, match));
    }

    /**
     * Ein grundlegender Match-Parser. Dieser ruft die Erfolgsmethode auf, wenn das übergebene Pattern erfolgreich gematcht
     * werden konnte. Die Erfolgsmethode gibt einen AST zurück mit dem Typ type und dem gematchten Match.
     * @param type Typ
     * @param regex Regular-Expression
     * @return Ein grundlegender Match-Parser
     * @param <TYPE> Typ
     * @param <ANNOTATIONS> ANNOTATION-Class beim Abstract Syntax Tree
     */
    static <TYPE, ANNOTATIONS> Parser<TYPE, ANNOTATIONS> match(TYPE type, String regex) {
        return match(type, Pattern.compile(regex));
    }
}
