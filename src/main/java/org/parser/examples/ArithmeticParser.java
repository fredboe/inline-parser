package org.parser.examples;

import org.parser.Consumable;
import org.parser.base.Parser;
import org.parser.base.build.*;
import org.parser.tree.AST;

import java.util.Optional;

public class ArithmeticParser implements Parser<ArithmeticParser.TYPE> {
    public enum TYPE {
        NUMBER, ADD, SUB, MUL, DIV, POT, FUNC, SIN, COS, TAN, PI, E
    }

    private final Parser<TYPE> aritParser;

    public ArithmeticParser() {
        aritParser = ArithmeticParser.arithmeticExample().getParser("EXPR");
    }

    @Override
    public Optional<AST<TYPE>> applyTo(Consumable consumable) {
        return aritParser.applyTo(consumable);
    }

    @Override
    public Optional<AST<TYPE>> applyTo(CharSequence sequence) {
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
     */
    public static ParserPool<TYPE> arithmeticExample() {
        ParserBuilder<TYPE> builder = new ParserBuilder<>();

        builder.newRule("ADD")
                .type(TYPE.ADD).rule("SUB").some(new Simplerule<TYPE>().hide("\\+").rule("SUB"))
                .or()
                .rule("SUB")
                .end();

        builder.newRule("SUB")
                .type(TYPE.SUB).rule("MUL").some(new Simplerule<TYPE>().hide("\\-").rule("MUL"))
                .or()
                .rule("MUL")
                .end();

        builder.newRule("MUL")
                .type(TYPE.MUL).rule("DIV").some(new Simplerule<TYPE>().hide("\\*").rule("DIV"))
                .or()
                .rule("DIV")
                .end();

        builder.newRule("DIV")
                .type(TYPE.DIV).rule("POT").some(new Simplerule<TYPE>().hide("/").rule("POT"))
                .or()
                .rule("POT")
                .end();

        builder.newRule("POT")
                .type(TYPE.POT).rule("SUBEXPR").some(new Simplerule<TYPE>().hide("\\^").rule("SUBEXPR"))
                .or()
                .rule("SUBEXPR")
                .end();

        builder.newRule("SUBEXPR")
                .rule("BRAC").or().rule("VAL").end();

        builder.newRule("VAL")
                .rule("NUMBER")
                .or()
                .type(TYPE.FUNC).rule("FUNC_SYMBOL").rule("BRAC")
                .or()
                .rule("CONST")
                .end();

        builder.newRule("BRAC")
                .type(Mode.justFst()).hide("\\(").rule("EXPR").hide("\\)")
                .end();

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

        /*builder.newRule("ADD")
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
                .concat().hide("\\(").rule("EXPR").hide("\\)")
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

        builder.newRule("EXPR").rule("ADD").end();*/

        return builder.build();
    }
}
