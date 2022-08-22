package org.parser.examples;

import org.parser.base.build.ParserBuilder;
import org.parser.base.build.ParserPool;

public class ArithmeticParser {
    public enum TYPE {
        NUMBER, ADD, SUB, MUL, DIV
    }

    /**
     * Grammar: <br>
     * NUMBER ::= [0-9] NUMBER | [0-9] <br>
     * EXPR ::= ADD <br>
     * ADD ::= SUB ("+" SUB)+ | SUB <br>
     * SUB ::= MUL ("-" MUL)+ | MUL <br>
     * MUL ::= DIV ("*" DIV)+ | DIV <br>
     * DIV ::= BRAC ("/" BRAC)+ | BRAC <br>
     * BRAC ::= "(" EXPR ")" | NUMBER <br>
     * @return Gibt einen ParserPool für arithmetische Ausdrücke zurück
     * @param <ANNOTATION> ANNOTATION-Typ des AST
     */
    public static <ANNOTATION> ParserPool<TYPE, ANNOTATION> arithmeticExample() {
        ParserBuilder<TYPE, ANNOTATION> builder = new ParserBuilder<>();

        builder.newRule("NUMBER").consistsOf().match(TYPE.NUMBER, "\\d+").end();

        builder.newRule("ADD").consistsOf()
                .concat(TYPE.ADD).rule("SUB").some().match("\\+").rule("SUB").someEnd()
                .or()
                .rule("SUB")
                .end();

        builder.newRule("SUB").consistsOf()
                .concat(TYPE.SUB).rule("MUL").some().match("\\-").rule("MUL").someEnd()
                .or()
                .rule("MUL")
                .end();

        builder.newRule("MUL").consistsOf()
                .concat(TYPE.MUL).rule("DIV").some().match("\\*").rule("DIV").someEnd()
                .or()
                .rule("DIV")
                .end();

        builder.newRule("DIV").consistsOf()
                .concat(TYPE.DIV).rule("BRAC").some().match("/").rule("BRAC").someEnd()
                .or()
                .rule("BRAC")
                .end();

        builder.newRule("BRAC").consistsOf()
                .concat().match("\\(").rule("EXPR").many("\\)")
                .or()
                .rule("NUMBER")
                .end();

        builder.newRule("EXPR").consistsOf().rule("ADD").end();

        return builder.build();
    }
}
