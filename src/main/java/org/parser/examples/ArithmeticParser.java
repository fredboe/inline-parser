package org.parser.examples;

import org.parser.Consumable;
import org.parser.base.Parser;
import org.parser.base.build.ParserBuilder;
import org.parser.base.build.ParserPool;
import org.parser.tree.AST;

import java.util.Optional;

public class ArithmeticParser<ANNOTATION> implements Parser<ArithmeticParser.TYPE, ANNOTATION> {
    public enum TYPE {
        NUMBER, ADD, SUB, MUL, DIV, POT, FUNC, SIN, COS, TAN, PI, E
    }

    private final Parser<TYPE, ANNOTATION> aritParser;

    public ArithmeticParser() {
        aritParser = ArithmeticParser.<ANNOTATION>arithmeticExample().getParser("EXPR");
    }

    @Override
    public Optional<AST<TYPE, ANNOTATION>> applyTo(Consumable consumable) {
        return aritParser.applyTo(consumable);
    }

    @Override
    public Optional<AST<TYPE, ANNOTATION>> applyTo(CharSequence sequence) {
        return applyTo(new Consumable(sequence,
                Consumable.Ignore.IGNORE_WHITESPACE, Consumable.Ignore.IGNORE_LINEBREAK)
        );
    }

    /**
     * Grammar: <br>
     * EXPR ::= ADD <br>
     * ADD ::= SUB ("+" SUB)+ | SUB <br>
     * SUB ::= MUL ("-" MUL)+ | MUL <br>
     * MUL ::= DIV ("*" DIV)+ | DIV <br>
     * DIV ::= POT ("/" POT)+ | POT <br>
     * POT ::= SUBEXPR ("^" SUBEXPR)+ | SUBEXPR <br>
     * SUBEXPR ::= BRAC | VAL <br>
     * VAL ::= NUMBER | FUNC_SYMBOl BRAC | CONST <br>
     * BRAC ::= "(" EXPR ")" <br>
     * NUMBER ::= (-)?\d+(\.\d*)?((e|E)(+|-)?\d+)? <br>
     * FUNC_SYMBOL ::= "sin" | "cos" | "tan" <br>
     * CONST ::= "pi" | "e" <br>
     * @return Returns a ParserPool for arithmetic expressions.
     * @param <ANNOTATION> ANNOTATION type of the AST
     */
    public static <ANNOTATION> ParserPool<TYPE, ANNOTATION> arithmeticExample() {
        ParserBuilder<TYPE, ANNOTATION> builder = new ParserBuilder<>();

        builder.newRule("ADD")
                .concat(TYPE.ADD).rule("SUB").some().match("\\+").rule("SUB").someEnd()
                .or()
                .rule("SUB")
                .end();

        builder.newRule("SUB")
                .concat(TYPE.SUB).rule("MUL").some().match("\\-").rule("MUL").someEnd()
                .or()
                .rule("MUL")
                .end();

        builder.newRule("MUL")
                .concat(TYPE.MUL).rule("DIV").some().match("\\*").rule("DIV").someEnd()
                .or()
                .rule("DIV")
                .end();

        builder.newRule("DIV")
                .concat(TYPE.DIV).rule("POT").some().match("/").rule("POT").someEnd()
                .or()
                .rule("POT")
                .end();

        builder.newRule("POT")
                .concat(TYPE.POT).rule("SUBEXPR").some().match("\\^").rule("SUBEXPR").someEnd()
                .or()
                .rule("SUBEXPR")
                .end();

        builder.newRule("SUBEXPR")
                .rule("BRAC").or().rule("VAL").end();

        builder.newRule("VAL")
                .rule("NUMBER")
                .or()
                .concat(TYPE.FUNC).rule("FUNC_SYMBOL").rule("BRAC")
                .or()
                .rule("CONST")
                .end();

        builder.newRule("BRAC")
                .concat().match("\\(").rule("EXPR").match("\\)")
                .end();

        // optional - then some digits, then optional . with digits and then optional exponent starting with e or E, optional +/- and then some digits
        builder.newRule("NUMBER")
                .match(TYPE.NUMBER, "(\\-)?\\d+(\\.\\d*)?((e|E)(\\+|\\-)?\\d+)?")
                .end();

        builder.newRule("FUNC_SYMBOL")
                .match(TYPE.SIN, "sin").or()
                .match(TYPE.COS, "cos").or()
                .match(TYPE.TAN, "tan").end();

        builder.newRule("CONST")
                        .match(TYPE.PI, "pi").or().match(TYPE.E, "e").end();

        builder.newRule("EXPR").rule("ADD").end();

        return builder.build();
    }
}
