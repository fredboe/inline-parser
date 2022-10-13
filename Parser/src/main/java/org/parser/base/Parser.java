package org.parser.base;

import org.parser.Consumable;
import org.parser.base.build.Mode;
import org.parser.tree.AST;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * Rules:
 * - A parser should consume the consumable only if the parser is successful.
 * - If a connection parser (ASTs as input) is implemented, the ASTs should be ignored,
 * where the ignore bit is set
 * - A parsing error should be passed using Optional (empty).
 * @param <TYPE> type
 */
public interface Parser<TYPE> {
    /**
     * Obtains a CharSequence and creates an AST from it.
     * @param consumable Consumable
     * @return An AST wrapped with Optional (empty if parsing error)
     */
    Optional<AST<TYPE>> applyTo(Consumable consumable);

    /**
     * Receives a CharSequence and creates an AST from it.
     * @param sequence CharSequence
     * @return An AST wrapped with optional (empty if parsing error)
     */
    default Optional<AST<TYPE>> applyTo(CharSequence sequence) {
        return applyTo(consumableOf(sequence));
    }

    default Consumable consumableOf(CharSequence sequence) {
        return new Consumable(sequence);
    }

    default Parser<TYPE> simplify() {
        return this;
    }

    /**
     * Basic Or parser with the passed parsers as subparsers and atSuccess is Parser.basicOrAtSuccess().
     * @param parsers Subparser.
     * @return Returns an Or parser with Parser.basicOrAtSuccess() as success method.
     */
    static <TYPE> OrParser<TYPE> or(List<Parser<TYPE>> parsers) {
        return new OrParser<>(basicOrAtSuccess(), parsers);
    }

    /**
     *
     * @param type Type of the created AST.
     * @param parsers subparser
     * @return Returns an Or parser with Parser.basicOrWithNodeAtSuccess(type) as success method.
     */
    static <TYPE> OrParser<TYPE> orWithNode(TYPE type, List<Parser<TYPE>> parsers) {
        return new OrParser<>(basicOrWithNodeAtSuccess(type), parsers);
    }

    /**
     *
     * @param type Type of the created AST.
     * @param parsers Subparser
     * @return Returns a concat parser with Parser.basicConcatAtSuccess(type) as success method.
     */
    static <TYPE> ConcatParser<TYPE> concat(TYPE type, List<Parser<TYPE>> parsers) {
        return new ConcatParser<>(basicConcatAtSuccess(type), parsers);
    }

    /**
     *
     * @param type Type of the created AST.
     * @param parser subparser
     * @return Returns a many-parser with the passed type and the passed parser as subparser.
     */
    static <TYPE> ManyParser<TYPE> many(TYPE type, Parser<TYPE> parser) {
        return new ManyParser<>(type, parser);
    }

    /**
     *
     * @param type Type of the created AST.
     * @param parser subparser
     * @return Returns a some-parser with the passed type and the passed parser as subparser
     */
    static <TYPE> Parser<TYPE> some(TYPE type, Parser<TYPE> parser) {
        return new ConcatParser<>(Mode.childrenIfNoType(type), List.of(parser, Parser.many(null, parser)));
    }

    /**
     *
     * @param parser subparser
     * @return Returns an optional-parser with the passed type and the passed parser as subparser.
     */
    static <TYPE> OptionalParser<TYPE> optional(Parser<TYPE> parser) {
        return new OptionalParser<>(parser);
    }


    /**
     * A basic hide parser. This calls the success method if the pattern passed in was successfully matched.
     * could be successfully matched. The success method simply returns an AST, with type set to the ignore bit.
     * @param pattern Pattern
     * @return A basic hide parser
     */
    static <TYPE> RegExParser<TYPE> hide(Pattern pattern) {
        return new RegExParser<>(pattern, basicHideAtSuccess());
    }

    /**
     * A basic hide parser. This calls the success method if the pattern passed in was successfully matched.
     * could be successfully matched. The success method simply returns an AST, with type set to the ignore bit.
     * @param regex Regular-Expression
     * @return A basic hide parser
     */
    static <TYPE> RegExParser<TYPE> hide(String regex) {
        return hide(Pattern.compile(regex));
    }

    /**
     * A basic keyword parser. This calls the success method if the pattern passed in was successfully matched.
     * could be matched. The success method returns an AST with type type where the match object is set to null.
     * @param type type
     * @param pattern Pattern
     * @return A basic keyword parser
     */
    static <TYPE> RegExParser<TYPE> keyword(TYPE type, Pattern pattern) {
        return new RegExParser<>(pattern, basicKeywordAtSuccess(type));
    }

    /**
     * A basic keyword parser. This calls the success method if the pattern passed in was successfully matched.
     * could be matched. The success method returns an AST with type type where the match object is set to null.
     * @param type type
     * @param regex Regular-Expression
     * @return A basic keyword parser
     */
    static <TYPE> RegExParser<TYPE> keyword(TYPE type, String regex) {
        return keyword(type, Pattern.compile(regex));
    }

    /**
     * A basic match parser. This calls the success method if the pattern passed in was successfully matched.
     * could be matched. The success method returns an AST with type type and the matched match.
     * @param type Type
     * @param pattern Pattern
     * @return A basic match parser
     */
    static <TYPE> RegExParser<TYPE> match(TYPE type, Pattern pattern) {
        return new RegExParser<>(pattern, basicMatchAtSuccess(type));
    }

    /**
     * A basic match parser. This calls the success method if the pattern passed in was successfully matched.
     * could be matched. The success method returns an AST with type type and the matched match.
     * @param type Type
     * @param regex regular expression
     * @return A basic match parser
     */
    static <TYPE> RegExParser<TYPE> match(TYPE type, String regex) {
        return match(type, Pattern.compile(regex));
    }


    /**
     *
     * @return Returns the identity function, because a normal Or parser does not have a type but
     * takes the type of the successful subparser.
     */
    static <TYPE> Function<AST<TYPE>, AST<TYPE>> basicOrAtSuccess() {
        return ast -> ast;
    }

    /**
     *
     * @param type Type of the resulting AST.
     * @return Returns a function that turns an AST A into an AST B with the passed type and A as child.
     */
    static <TYPE> Function<AST<TYPE>, AST<TYPE>> basicOrWithNodeAtSuccess(TYPE type) {
        return ast -> new AST<>(type).addChild(ast);
    }

    /**
     * If any of the child nodes have type null, this AST will be replaced by its children in the list.
     * @param type Type of the resulting AST.
     * @return Returns a function that creates an AST B from multiple ASTs with the passed type and the
     * ASTs as children.
     */
    static <TYPE> Function<List<AST<TYPE>>, AST<TYPE>> basicConcatAtSuccess(TYPE type) {
        return trees -> new AST<>(type, trees);
    }

    /**
     *
     * @param type Type of the resulting AST.
     * @return Returns a function that creates an AST from a match that has the passed type, and
     * the match as "match".
     */
    static <TYPE> Function<Consumable.Match, AST<TYPE>> basicMatchAtSuccess(TYPE type) {
        return match -> new AST<>(type, match);
    }

    /**
     *
     * @return Returns a function that creates an AST from a match with the ignore bit set.
     */
    static <TYPE> Function<Consumable.Match, AST<TYPE>> basicHideAtSuccess() {
        return match -> new AST<TYPE>(null).setIgnore(true);
    }

    /**
     *
     * @param type Type of the resulting AST.
     * @return Returns a function which creates an AST from a match which has the passed type
     * but the match of the AST is null.
     */
    static <TYPE> Function<Consumable.Match, AST<TYPE>> basicKeywordAtSuccess(TYPE type) {
        return match -> new AST<>(type);
    }
}
