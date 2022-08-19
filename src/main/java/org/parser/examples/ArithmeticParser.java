package org.parser.examples;

import org.parser.base.build.ParserBuilder;
import org.parser.base.build.ParserPool;

public class ArithmeticParser {
    public enum TYPE {
        NUMBER, ADD, SUB, MUL, DIV
    }

    /**
     * Grammar:
     * <number> ::= [0-9] <number> | [0-9]
     * <factor> ::= ( <expr> ) | <number>
     * <term> ::= <factor> * <term> | <factor> / <term> | <number>
     * <expr> ::= <term> + <expr> | <term> - <expr> | <term>
     * @return Gibt einen ParserPool für arithmetische Ausdrücke zurück
     * @param <ANNOTATION> ANNOTATION-Typ des AST
     */
    public static <ANNOTATION> ParserPool<TYPE, ANNOTATION> arithmeticParser() {
        /*

         */
        ParserBuilder<TYPE, ANNOTATION> builder = new ParserBuilder<>();
        builder.newRule("NUMBER").consistsOf().match(TYPE.NUMBER, "\\d+").end();

        builder.newRule("FACTOR").consistsOf()
                .concat().hide("\\(").rule("EXPR").hide("\\)")
                .or()
                .rule("NUMBER")
                .end();

        builder.newRule("TERM").consistsOf()
                .concat(TYPE.MUL).rule("FACTOR").hide("\\*").rule("TERM")
                .or()
                .concat(TYPE.DIV).rule("FACTOR").hide("/").rule("TERM")
                .or()
                .rule("FACTOR")
                .end();

        builder.newRule("EXPR").consistsOf()
                .concat(TYPE.ADD).rule("TERM").hide("\\+").rule("EXPR")
                .or()
                .concat(TYPE.SUB).rule("TERM").hide("\\-").rule("EXPR")
                .or()
                .rule("TERM")
                .end();

        return builder.build();
    }
}
