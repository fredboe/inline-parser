package org.parser.base;

import org.parser.Consumable;
import org.parser.tree.AST;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
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
     * Erhält eine CharSequence und erzeugt aus dieser einen AST.
     * @param sequence CharSequence
     * @return Ein AST mit Optional gewrappt (empty falls Parsing-Fehler)
     */
    default Optional<AST<TYPE, ANNOTATION>> applyTo(CharSequence sequence) {
        return applyTo(new Consumable(sequence));
    }

    static <TYPE, ANNOTATION> OrParser<TYPE, ANNOTATION> or(List<Parser<TYPE, ANNOTATION>> parsers) {
        return new OrParser<>(basicOrAtSuccess(), parsers);
    }

    static <TYPE, ANNOTATION> OrParser<TYPE, ANNOTATION> orWithNode(TYPE type, List<Parser<TYPE, ANNOTATION>> parsers) {
        return new OrParser<>(basicOrWithNodeAtSuccess(type), parsers);
    }

    static <TYPE, ANNOTATION> ConcatParser<TYPE, ANNOTATION> concat(TYPE type, List<Parser<TYPE, ANNOTATION>> parsers) {
        return new ConcatParser<>(basicConcatAtSuccess(type), parsers);
    }

    static <TYPE, ANNOTATION> ManyParser<TYPE, ANNOTATION> many(TYPE type, Parser<TYPE, ANNOTATION> parser) {
        return new ManyParser<>(type, parser);
    }

    static <TYPE, ANNOTATION> SomeParser<TYPE, ANNOTATION> some(TYPE type, Parser<TYPE, ANNOTATION> parser) {
        return new SomeParser<>(type, parser);
    }

    /**
     * Ein grundlegender Hide-Parser. Dieser ruft die Erfolgsmethode auf, wenn das übergebene Pattern erfolgreich gematcht
     * werden konnte. Die Erfolgsmethode gibt einfach einen AST zurück, mit dem Typ type, bei dem das ignore-Bit gesetzt ist.
     * @param pattern Pattern
     * @return Ein grundlegender Hide-Parser
     */
    static <TYPE, ANNOTATION> RegExParser<TYPE, ANNOTATION> hide(Pattern pattern) {
        return new RegExParser<>(pattern, basicHideAtSuccess());
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
        return new RegExParser<>(pattern, basicKeywordAtSuccess(type));
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
        return new RegExParser<>(pattern, basicMatchAtSuccess(type));
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


    /**
     *
     * @return Gibt die Identitätsfunktion zurück, denn ein normaler Or-Parser besitzt keinen Typ, sondern
     * übernimmt den Typen des erfolgreichen Subparsers.
     */
    static <TYPE, ANNOTATION> Function<AST<TYPE, ANNOTATION>, AST<TYPE, ANNOTATION>> basicOrAtSuccess() {
        return ast -> ast;
    }

    /**
     *
     * @param type Typ des resultierenden AST
     * @return Gibt eine Funktion zurück, die aus einem AST A einen AST B mit dem übergebenen Typen und A als Kind macht.
     *
     */
    static <TYPE, ANNOTATION> Function<AST<TYPE, ANNOTATION>, AST<TYPE, ANNOTATION>> basicOrWithNodeAtSuccess(TYPE type) {
        return ast -> new AST<TYPE, ANNOTATION>(type, null).addChild(ast);
    }

    /**
     *
     * @param type Typ des resultierenden AST
     * @return Gibt eine Funktion zurück, die aus mehreren ASTs einen AST B erstellt mit dem übergebenen Typen und den
     * ASTs als Kindern.
     */
    static <TYPE, ANNOTATION> Function<List<AST<TYPE, ANNOTATION>>, AST<TYPE, ANNOTATION>> basicConcatAtSuccess(TYPE type) {
        return trees -> new AST<>(type, null, trees);
    }

    /**
     *
     * @param type Typ des resultierenden AST
     * @return Gibt eine Funktion zurück, die aus einem Match einen AST erzeugt, der den übergebenen Typen hat und
     * das Match als "Match".
     */
    static <TYPE, ANNOTATION> Function<Consumable.Match, AST<TYPE, ANNOTATION>> basicMatchAtSuccess(TYPE type) {
        return match -> new AST<>(type, match);
    }

    /**
     *
     * @return Gibt eine Funktion zurück, die aus einem Match einen AST erzeugt, bei dem das Ignore-Bit gesetzt ist.
     */
    static <TYPE, ANNOTATION> Function<Consumable.Match, AST<TYPE, ANNOTATION>> basicHideAtSuccess() {
        return match -> new AST<TYPE, ANNOTATION>(null).setIgnore(true);
    }

    /**
     *
     * @param type Typ des resultierenden AST
     * @return Gibt eine Funktion zurück, die aus einem Match einen AST erzeugt, der den übergebenen Typen hat
     * und das Match des ASTs ist aber null.
     */
    static <TYPE, ANNOTATION> Function<Consumable.Match, AST<TYPE, ANNOTATION>> basicKeywordAtSuccess(TYPE type) {
        return match -> new AST<>(type, null);
    }
}
