package org.parser.base;

import org.parser.Consumable;
import org.parser.tree.AST;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Rules:
 * - A parser should consume the consumable only if the parser is successful.
 * - If a connection parser (ASTs as input) is implemented, the ASTs should be ignored,
 * where the ignore bit is set
 * - A parsing error should be passed using Optional (empty).
 * @param <TYPE> type
 * @param <ANNOTATION> ANNOTATION-Class at Abstract Syntax Tree
 */
public interface Parser<TYPE, ANNOTATION> {
    /**
     * Obtains a CharSequence and creates an AST from it.
     * @param consumable Consumable
     * @return An AST wrapped with Optional (empty if parsing error)
     */
    Optional<AST<TYPE, ANNOTATION>> applyTo(Consumable consumable);

    /**
     * Receives a CharSequence and creates an AST from it.
     * @param sequence CharSequence
     * @return An AST wrapped with optional (empty if parsing error)
     */
    default Optional<AST<TYPE, ANNOTATION>> applyTo(CharSequence sequence) {
        return applyTo(new Consumable(sequence));
    }

    /**
     * Basic Or parser with the passed parsers as subparsers and atSuccess is Parser.basicOrAtSuccess().
     * @param parsers Subparser.
     * @return Returns an Or parser with Parser.basicOrAtSuccess() as success method.
     */
    static <TYPE, ANNOTATION> OrParser<TYPE, ANNOTATION> or(List<Parser<TYPE, ANNOTATION>> parsers) {
        return new OrParser<>(basicOrAtSuccess(), parsers);
    }

    /**
     *
     * @param type Type of the created AST.
     * @param parsers subparser
     * @return Returns an Or parser with Parser.basicOrWithNodeAtSuccess(type) as success method.
     */
    static <TYPE, ANNOTATION> OrParser<TYPE, ANNOTATION> orWithNode(TYPE type, List<Parser<TYPE, ANNOTATION>> parsers) {
        return new OrParser<>(basicOrWithNodeAtSuccess(type), parsers);
    }

    /**
     *
     * @param type Type of the created AST.
     * @param parsers Subparser
     * @return Returns a concat parser with Parser.basicConcatAtSuccess(type) as success method.
     */
    static <TYPE, ANNOTATION> ConcatParser<TYPE, ANNOTATION> concat(TYPE type, List<Parser<TYPE, ANNOTATION>> parsers) {
        return new ConcatParser<>(basicConcatAtSuccess(type), parsers);
    }

    /**
     *
     * @param type Type of the created AST.
     * @param parser subparser
     * @return Returns a many-parser with the passed type and the passed parser as subparser.
     */
    static <TYPE, ANNOTATION> ManyParser<TYPE, ANNOTATION> many(TYPE type, Parser<TYPE, ANNOTATION> parser) {
        return new ManyParser<>(type, parser);
    }

    /**
     * A basic hide parser. This calls the success method if the pattern passed in was successfully matched.
     * could be successfully matched. The success method simply returns an AST, with type set to the ignore bit.
     * @param pattern Pattern
     * @return A basic hide parser
     */
    static <TYPE, ANNOTATION> RegExParser<TYPE, ANNOTATION> hide(Pattern pattern) {
        return new RegExParser<>(pattern, basicHideAtSuccess());
    }

    /**
     * A basic hide parser. This calls the success method if the pattern passed in was successfully matched.
     * could be successfully matched. The success method simply returns an AST, with type set to the ignore bit.
     * @param regex Regular-Expression
     * @return A basic hide parser
     */
    static <TYPE, ANNOTATION> RegExParser<TYPE, ANNOTATION> hide(String regex) {
        return hide(Pattern.compile(regex));
    }

    /**
     * A basic keyword parser. This calls the success method if the pattern passed in was successfully matched.
     * could be matched. The success method returns an AST with type type where the match object is set to null.
     * @param type type
     * @param pattern Pattern
     * @return A basic keyword parser
     */
    static <TYPE, ANNOTATION> RegExParser<TYPE, ANNOTATION> keyword(TYPE type, Pattern pattern) {
        return new RegExParser<>(pattern, basicKeywordAtSuccess(type));
    }

    /**
     * A basic keyword parser. This calls the success method if the pattern passed in was successfully matched.
     * could be matched. The success method returns an AST with type type where the match object is set to null.
     * @param type type
     * @param regex Regular-Expression
     * @return A basic keyword parser
     */
    static <TYPE, ANNOTATION> RegExParser<TYPE, ANNOTATION> keyword(TYPE type, String regex) {
        return keyword(type, Pattern.compile(regex));
    }

    /**
     * A basic match parser. This calls the success method if the pattern passed in was successfully matched.
     * could be matched. The success method returns an AST with type type and the matched match.
     * @param type Type
     * @param pattern Pattern
     * @return A basic match parser
     */
    static <TYPE, ANNOTATION> RegExParser<TYPE, ANNOTATION> match(TYPE type, Pattern pattern) {
        return new RegExParser<>(pattern, basicMatchAtSuccess(type));
    }

    /**
     * A basic match parser. This calls the success method if the pattern passed in was successfully matched.
     * could be matched. The success method returns an AST with type type and the matched match.
     * @param type Type
     * @param regex regular expression
     * @return A basic match parser
     */
    static <TYPE, ANNOTATION> RegExParser<TYPE, ANNOTATION> match(TYPE type, String regex) {
        return match(type, Pattern.compile(regex));
    }


    /**
     *
     * @return Returns the identity function, because a normal Or parser does not have a type but
     * takes the type of the successful subparser.
     */
    static <TYPE, ANNOTATION> Function<AST<TYPE, ANNOTATION>, AST<TYPE, ANNOTATION>> basicOrAtSuccess() {
        return ast -> ast;
    }

    /**
     *
     * @param type Type of the resulting AST.
     * @return Returns a function that turns an AST A into an AST B with the passed type and A as child.
     */
    static <TYPE, ANNOTATION> Function<AST<TYPE, ANNOTATION>, AST<TYPE, ANNOTATION>> basicOrWithNodeAtSuccess(TYPE type) {
        return ast -> new AST<TYPE, ANNOTATION>(type, null).addChild(ast);
    }

    /**
     * If any of the child nodes have type null, this AST will be replaced by its children in the list.
     * @param type Type of the resulting AST.
     * @return Returns a function that creates an AST B from multiple ASTs with the passed type and the
     * ASTs as children.
     */
    static <TYPE, ANNOTATION> Function<List<AST<TYPE, ANNOTATION>>, AST<TYPE, ANNOTATION>> basicConcatAtSuccess(TYPE type) {
        return trees -> {
            var children = trees.stream().map(tree -> tree.getType() == null ? tree.getChildren() : List.of(tree))
                  .flatMap(Collection::stream).collect(Collectors.toList());
            return new AST<>(type, null, children);
        };
    }

    /**
     *
     * @param type Type of the resulting AST.
     * @return Returns a function that creates an AST from a match that has the passed type, and
     * the match as "match".
     */
    static <TYPE, ANNOTATION> Function<Consumable.Match, AST<TYPE, ANNOTATION>> basicMatchAtSuccess(TYPE type) {
        return match -> new AST<>(type, match);
    }

    /**
     *
     * @return Returns a function that creates an AST from a match with the ignore bit set.
     */
    static <TYPE, ANNOTATION> Function<Consumable.Match, AST<TYPE, ANNOTATION>> basicHideAtSuccess() {
        return match -> new AST<TYPE, ANNOTATION>(null).setIgnore(true);
    }

    /**
     *
     * @param type Type of the resulting AST.
     * @return Returns a function which creates an AST from a match which has the passed type
     * but the match of the AST is null.
     */
    static <TYPE, ANNOTATION> Function<Consumable.Match, AST<TYPE, ANNOTATION>> basicKeywordAtSuccess(TYPE type) {
        return match -> new AST<>(type, null);
    }
}
