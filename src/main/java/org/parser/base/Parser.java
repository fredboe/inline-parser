package org.parser.base;

import org.parser.Consumable;
import org.parser.tree.AST;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

// operator parser
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
     * Erhält eine CharSequence und erzeugt aus dieser einen AST.
     * @param sequence CharSequence
     * @return Ein AST mit Optional gewrappt (empty falls Parsing-Fehler)
     */
    default Optional<AST<TYPE, ANNOTATION>> applyTo(CharSequence sequence) {
        return applyTo(new Consumable(sequence));
    }

    static <TYPE, ANNOTATION> OrParser<TYPE, ANNOTATION> or(List<Parser<TYPE, ANNOTATION>> parsers) {
        return new OrParser<>(ast -> ast , parsers);
    }

    static <TYPE, ANNOTATION> OrParser<TYPE, ANNOTATION> orWithNode(TYPE type, List<Parser<TYPE, ANNOTATION>> parsers) {
        return new OrParser<>(ast -> new AST<TYPE, ANNOTATION>(type, null).addChild(ast), parsers);
    }

    static <TYPE, ANNOTATION> ConcatParser<TYPE, ANNOTATION> concat(TYPE type, List<Parser<TYPE, ANNOTATION>> parsers) {
        return new ConcatParser<>(trees -> new AST<>(type, null, trees), parsers);
    }

    /**
     * Ein grundlegender Hide-Parser. Dieser ruft die Erfolgsmethode auf, wenn das übergebene Pattern erfolgreich gematcht
     * werden konnte. Die Erfolgsmethode gibt einfach einen AST zurück, mit dem Typ type, bei dem das ignore-Bit gesetzt ist.
     * @param pattern Pattern
     * @return Ein grundlegender Hide-Parser
     */
    static <TYPE, ANNOTATION> RegExParser<TYPE, ANNOTATION> hide(Pattern pattern) {
        return new RegExParser<>(pattern, match -> new AST<TYPE, ANNOTATION>(null).setIgnore(true));
    }

    /**
     * Ein grundlegender Hide-Parser. Dieser ruft die Erfolgsmethode auf, wenn das übergebene Pattern erfolgreich gematcht
     * werden konnte. Die Erfolgsmethode gibt einfach einen AST zurück, mit dem Typ type, bei dem das ignore-Bit gesetzt ist.
     * @param regex Regular-Expression
     * @return Ein grundlegender Hide-Parser
     */
    static <TYPE, ANNOTATION> RegExParser<TYPE, ANNOTATION> hide(String regex) {
        return hide(Pattern.compile(regex));
    }

    /**
     * Ein grundlegender Keyword-Parser. Dieser ruft die Erfolgsmethode auf, wenn das übergebene Pattern erfolgreich gematcht
     * werden konnte. Die Erfolgsmethode gibt einen AST zurück mit dem Typ type, bei dem das Match-Objekt auf null gesetzt ist.
     * @param type Typ
     * @param pattern Pattern
     * @return Ein grundlegender Keyword-Parser
     */
    static <TYPE, ANNOTATION> RegExParser<TYPE, ANNOTATION> keyword(TYPE type, Pattern pattern) {
        return new RegExParser<>(pattern, match -> new AST<>(type, null));
    }

    /**
     * Ein grundlegender Keyword-Parser. Dieser ruft die Erfolgsmethode auf, wenn das übergebene Pattern erfolgreich gematcht
     * werden konnte. Die Erfolgsmethode gibt einen AST zurück mit dem Typ type, bei dem das Match-Objekt auf null gesetzt ist.
     * @param type Typ
     * @param regex Regular-Expression
     * @return Ein grundlegender Keyword-Parser
     */
    static <TYPE, ANNOTATION> RegExParser<TYPE, ANNOTATION> keyword(TYPE type, String regex) {
        return keyword(type, Pattern.compile(regex));
    }

    /**
     * Ein grundlegender Match-Parser. Dieser ruft die Erfolgsmethode auf, wenn das übergebene Pattern erfolgreich gematcht
     * werden konnte. Die Erfolgsmethode gibt einen AST zurück mit dem Typ type und dem gematchten Match.
     * @param type Typ
     * @param pattern Pattern
     * @return Ein grundlegender Match-Parser
     */
    static <TYPE, ANNOTATION> RegExParser<TYPE, ANNOTATION> match(TYPE type, Pattern pattern) {
        return new RegExParser<>(pattern, match -> new AST<>(type, match));
    }

    /**
     * Ein grundlegender Match-Parser. Dieser ruft die Erfolgsmethode auf, wenn das übergebene Pattern erfolgreich gematcht
     * werden konnte. Die Erfolgsmethode gibt einen AST zurück mit dem Typ type und dem gematchten Match.
     * @param type Typ
     * @param regex Regular-Expression
     * @return Ein grundlegender Match-Parser
     */
    static <TYPE, ANNOTATION> RegExParser<TYPE, ANNOTATION> match(TYPE type, String regex) {
        return match(type, Pattern.compile(regex));
    }
}
