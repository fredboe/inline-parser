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
    public Consumable consumableOf(CharSequence sequence) {
        return new Consumable(sequence, Consumable.Ignore.IGNORE_WHITESPACE, Consumable.Ignore.IGNORE_COMMENT);
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

        return builder.build();
    }
}
